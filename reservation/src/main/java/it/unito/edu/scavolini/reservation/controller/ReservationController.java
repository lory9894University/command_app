package it.unito.edu.scavolini.reservation.controller;

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

import java.time.LocalDateTime;


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PreparationRepository preparationRepository;

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        reservation.setTableNum("ND"); // table num should be decided by the restaurant
        reservation.setState(ReservationStateEnum.WAITING);

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

        if (reservationOrder.getDateTime() == null) {
            reservationOrder.setDateTime(LocalDateTime.now());
        } else {
            reservationOrder.setDateTime(reservationOrder.getDateTime());
        }

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
            newPreparation.setTableNum(preparation.getTableNum());
            newPreparation.setState(PreparationStatesEnum.WAITING);

            Preparation savedPreparation = preparationRepository.save(newPreparation);
        }

        return ResponseEntity.ok(savedReservation);
    }



}
