package it.unito.edu.scavolini.reservation.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import it.unito.edu.scavolini.reservation.enums.OrderStateEnum;
import it.unito.edu.scavolini.reservation.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.reservation.enums.ReservationStateEnum;
import it.unito.edu.scavolini.reservation.model.*;
import it.unito.edu.scavolini.reservation.repository.OrderRepository;
import it.unito.edu.scavolini.reservation.repository.PreparationRepository;
import it.unito.edu.scavolini.reservation.repository.ReservationRepository;
import it.unito.edu.scavolini.reservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PreparationRepository preparationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RabbitMqSender rabbitMqSender;

    private boolean authEnabled = true;

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        User reservationUser = reservation.getUser();
        if (authEnabled) {
            FirebaseToken firebaseToken = checkFirebaseAuth(reservation.getUser().getUserId());
            if (firebaseToken == null) {
                return ResponseEntity.badRequest().build();
            }

            reservationUser.setUserId(firebaseToken.getUid());
            reservationUser.setUsername(firebaseToken.getName());
        }

        User savedUser = userRepository.save(reservationUser);

        reservation.setUser(savedUser);


        reservation.setTableNum("ND"); // table num should be decided by the restaurant
        reservation.setState(ReservationStateEnum.WAITING);

        if (reservation.getOrder() != null) {
            return ResponseEntity.badRequest().build();
        }


        Reservation savedReservation = reservationRepository.save(reservation);

        savedReservation.setReservationName(savedReservation.getUser().getUsername());
        return ResponseEntity.ok(savedReservation);
    }

    @PostMapping(value = "/create/preorder", consumes = "application/json")
    public ResponseEntity<Reservation> createPreorder(@RequestBody Reservation reservation) {

        //////////////////////////// User handling ////////////////////////////
        User reservationUser = reservation.getUser();
        if (authEnabled) {
            FirebaseToken firebaseToken = checkFirebaseAuth(reservation.getUser().getUserId());
            if (firebaseToken == null) {
                return ResponseEntity.badRequest().build();
            }

            reservationUser.setUserId(firebaseToken.getUid());
            reservationUser.setUsername(firebaseToken.getName());
        }

        User savedUser = userRepository.save(reservationUser);


        //////////////////////////// Reservation handling ////////////////////////////
        reservation.setTableNum("ND"); // table num should be decided by the restaurant
        reservation.setState(ReservationStateEnum.WAITING);

        // if this is not a Preorder (does not contain an order) send error
        if (reservation.getOrder() == null){
            return ResponseEntity.badRequest().build(); // TODO send detailer error
        }


        //////////////////////////// Reservation Order handling ////////////////////////////
        Order reservationOrder = reservation.getOrder();

        // check Order to match information of Preorder
        if (reservationOrder.getDateTime() == null ||
            !reservationOrder.getDateTime().equals(reservation.getDateTime())) {
            // reservation and order datetime should match
            return ResponseEntity.badRequest().build(); // TODO send detailer error
            // other strategy: to force order date time to match reservation date time
            // reservationOrder.setDateTime(reservation.getDateTime());
        }

        // set and save Order information while waiting acceptance or rejection
        reservationOrder.setOrderUsername(reservationUser.getUsername()); // not needed
        reservationOrder.setTableNum("ND");
        reservationOrder.setOrderState(OrderStateEnum.WAITING);

        // set Order User
        reservationOrder.setUser(savedUser);

        // all Order data is set --> save Order
        Order savedOrder = orderRepository.save(reservationOrder);

        // set Reservation Order and User
        reservation.setUser(savedUser);
        reservation.setOrder(savedOrder);

        // all Reservation data is set --> save Reservation
        Reservation savedReservation = reservationRepository.save(reservation);


        //////////////////////////// Order Preparations handling ////////////////////////////
        reservationOrder = savedReservation.getOrder();

        // save single preparations to DB
        for (Preparation preparation : reservationOrder.getPreparationList()) {
            // TODO check if it is necessary to recreate the preparation
            Preparation newPreparation = new Preparation();
            newPreparation.setOrder(savedOrder);
            newPreparation.setName(preparation.getName());
            newPreparation.setTableNum("ND");
            newPreparation.setState(PreparationStatesEnum.WAITING);

            Preparation savedPreparation = preparationRepository.save(newPreparation);
        }

        // set User information (transient) in Reservation and Order JSON just to return it to the client
        savedReservation.setReservationName(savedReservation.getUser().getUsername());
        savedReservation.getOrder().setOrderUsername(savedReservation.getUser().getUsername());
        UserTransient userTransient = new UserTransient();
        userTransient.setUsername(savedReservation.getUser().getUsername());
        userTransient.setUserId(savedReservation.getUser().getUserId());
        savedReservation.getOrder().setUserTransient(userTransient);
        return ResponseEntity.ok(savedReservation);
    }

    @PutMapping(value = "/accept/{id}/table/{tableNum}")
    public ResponseEntity<Reservation> acceptReservation(@PathVariable Long id, @PathVariable String tableNum) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        // set Order as accepted and save table number assigned
        reservation.setState(ReservationStateEnum.ACCEPTED);
        reservation.setTableNum(tableNum);
        Reservation savedReservation = reservationRepository.save(reservation);

        // if there is an Order this is a Preorder, the Order should be sent to Order management
        acceptAndSendOrder(tableNum, savedReservation);

        savedReservation.setReservationName(savedReservation.getUser().getUsername());
        savedReservation.getOrder().setOrderUsername(savedReservation.getUser().getUsername());
        return ResponseEntity.ok(savedReservation);
    }

    private void acceptAndSendOrder(String tableNum, Reservation savedReservation) {
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

            // set userTransient field to send it to order management (user field is not serialized in JSON)
            UserTransient userTransient = new UserTransient();
            userTransient.setUsername(savedReservation.getUser().getUsername());
            userTransient.setUserId(savedReservation.getUser().getUserId());
            savedOrder.setUserTransient(userTransient);
            rabbitMqSender.sendPreorder(savedOrder);
        }
    }

    @PutMapping(value = "/reject/{id}")
    public ResponseEntity<Reservation> rejectReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        reservation.setState(ReservationStateEnum.REJECTED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // if there is an Order this is a Preorder, the Order should be sent to Order management
        // TODO: It could not be sent because it's rejected, to decide
        Order reservationOrder = savedReservation.getOrder();
        if (reservationOrder != null){
            reservationOrder.setOrderState(OrderStateEnum.REJECTED);
            Order savedOrder = orderRepository.save(reservationOrder);
            rabbitMqSender.sendPreorder(savedOrder);
        }

        savedReservation.setReservationName(savedReservation.getUser().getUsername());
        return ResponseEntity.ok(savedReservation);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<List<Reservation>> getAll(){
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            reservation.setReservationName(reservation.getUser().getUsername());
        }

        return ResponseEntity.ok(reservations);
    }

    @GetMapping(value = "/all/waiting")
    public ResponseEntity<List<Reservation>> getAllWaiting(){
        List<Reservation> reservations = reservationRepository.findAllByState(ReservationStateEnum.WAITING);

        for (Reservation reservation : reservations) {
            reservation.setReservationName(reservation.getUser().getUsername());
        }

        return ResponseEntity.ok(reservations);
    }

    @GetMapping(value = "/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        // get only reservations (not preorders): aka reservations without orders
        List<Reservation> reservations = reservationRepository.findAllByOrderNull();

        for (Reservation reservation : reservations) {
            reservation.setReservationName(reservation.getUser().getUsername());
        }

        return ResponseEntity.ok(reservations);
    }

    @GetMapping(value = "/preorders")
    public ResponseEntity<List<Reservation>> getPreorders() {
        // get only preorders (not just reservations): aka reservations with orders
        List<Reservation> reservations = reservationRepository.findAllByOrderNotNull();

        for (Reservation reservation : reservations) {
            reservation.setReservationName(reservation.getUser().getUsername());
        }

        return ResponseEntity.ok(reservations);
    }

    private FirebaseToken checkFirebaseAuth(String idToken){
        FirebaseToken decodedToken = null;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            return null;
        }
        return decodedToken;
    }

    @GetMapping(value = "/switchauth")
    public ResponseEntity<String> switchAuth(){ // TODO: INSECURE - USED TO TEST - DELETE
        this.authEnabled = !this.authEnabled;
        return ResponseEntity.ok("||||||||||||| Auth enabled: " + this.authEnabled + " |||||||||||||");
    }



}
