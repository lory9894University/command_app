package it.unito.edu.scavolini.reservation.controller;

import it.unito.edu.scavolini.reservation.enums.OrderStateEnum;
import it.unito.edu.scavolini.reservation.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.reservation.enums.ReservationStateEnum;
import it.unito.edu.scavolini.reservation.model.Order;
import it.unito.edu.scavolini.reservation.model.Preparation;
import it.unito.edu.scavolini.reservation.model.Reservation;
import it.unito.edu.scavolini.reservation.repository.OrderRepository;
import it.unito.edu.scavolini.reservation.repository.PreparationRepository;
import it.unito.edu.scavolini.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PreparationRepository preparationRepository;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        reservation.setTableNum("ND"); // table num should be decided by the restaurant
        reservation.setState(ReservationStateEnum.WAITING);

        if (reservation.getOrder() != null){
            return ResponseEntity.badRequest().build();
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return ResponseEntity.ok(savedReservation);
    }

    @PostMapping(value = "/create/preorder", consumes = "application/json")
    public ResponseEntity<Reservation> createPreorder(@RequestBody Reservation reservation) {
        reservation.setTableNum("ND"); // table num should be decided by the restaurant
        reservation.setState(ReservationStateEnum.WAITING);
        if (reservation.getOrder() == null){
            return ResponseEntity.badRequest().build(); // TODO send detailer error
        }
        Order reservationOrder = reservation.getOrder();

        // checking order to match information of preorder
        if (reservationOrder.getDateTime() == null ||
            !reservationOrder.getDateTime().equals(reservation.getDateTime())) {
            // reservation and order datetime should match
            return ResponseEntity.badRequest().build(); // TODO send detailer error
            // other strategy: to force order date time to match reservation date time
            // reservationOrder.setDateTime(reservation.getDateTime());
        }

        reservationOrder.setTableNum("ND");
        reservationOrder.setOrderState(OrderStateEnum.WAITING);
        Order savedOrder = orderRepository.save(reservationOrder);

        reservation.setOrder(savedOrder);
        Reservation savedReservation = reservationRepository.save(reservation);

        reservationOrder = savedReservation.getOrder();

        // sending single preparations of the order to the kitchen
        for (Preparation preparation : reservationOrder.getPreparationList()) {
            // TODO check if it is necessary to recreate the preparation
            Preparation newPreparation = new Preparation();
            newPreparation.setOrder(savedOrder);
            newPreparation.setName(preparation.getName());
            newPreparation.setTableNum("ND");
            newPreparation.setState(PreparationStatesEnum.WAITING);

            Preparation savedPreparation = preparationRepository.save(newPreparation);
        }

        return ResponseEntity.ok(savedReservation);
    }

    @PutMapping(value = "/accept/{id}/table/{tableNum}")
    public ResponseEntity<Reservation> acceptReservation(@PathVariable Long id, @PathVariable String tableNum) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        reservation.setState(ReservationStateEnum.ACCEPTED);
        reservation.setTableNum(tableNum);
        Reservation savedReservation = reservationRepository.save(reservation);

        // if there is a order this is a Preorder, the Order should be sent to Order management
        Order reservationOrder = savedReservation.getOrder();
        if (reservationOrder != null){
            reservationOrder.setTableNum(tableNum);
            reservationOrder.setOrderState(OrderStateEnum.ACCEPTED);
            Order savedOrder = orderRepository.save(reservationOrder);
            List<Preparation> preparationList = savedOrder.getPreparationList();
            for (Preparation preparation : preparationList) {
                preparation.setTableNum(tableNum);
                preparationRepository.save(preparation);
            }
            rabbitMqSender.sendPreorder(savedOrder);
        }

        return ResponseEntity.ok(savedReservation);
    }

    @PutMapping(value = "/reject/{id}")
    public ResponseEntity<Reservation> rejectReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        reservation.setState(ReservationStateEnum.REJECTED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // if there is a order this is a Preorder, the Order should be sent to Order management
        // TODO: It could not be sent, to decide
        Order reservationOrder = savedReservation.getOrder();
        if (reservationOrder != null){
            reservationOrder.setOrderState(OrderStateEnum.REJECTED);
            Order savedOrder = orderRepository.save(reservationOrder);
            rabbitMqSender.sendPreorder(savedOrder);
        }

        return ResponseEntity.ok(savedReservation);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<List<Reservation>> getAll(){
        List<Reservation> reservations = reservationRepository.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping(value = "/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        // get only reservations (not preorders): aka reservations without orders
        List<Reservation> reservations = reservationRepository.findAllByOrderNull();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping(value = "/preorders")
    public ResponseEntity<List<Reservation>> getPreorders() {
        // get only preorders (not just reservations): aka reservations with orders
        List<Reservation> reservations = reservationRepository.findAllByOrderNotNull();
        return ResponseEntity.ok(reservations);
    }



}
