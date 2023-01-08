package it.unito.edu.scavolini.order_management.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.enums.OrderTypeEnum;
import it.unito.edu.scavolini.order_management.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.model.User;
import it.unito.edu.scavolini.order_management.repository.OrderRepository;
import it.unito.edu.scavolini.order_management.repository.PreparationRepository;
import it.unito.edu.scavolini.order_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitMqSender rabbitMqSender;

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(){

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getUser() != null) {
                order.setOrderName(order.getUser().getUsername());
            }
        }

        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Order> removeOrderById(@RequestParam("id") Long id) {
        Order orderToRemove = orderRepository.findDistinctFirstById(id);
        orderRepository.delete(orderToRemove);
        return ResponseEntity.ok(orderToRemove);
    }

    /**
     * Creates an Order, saves in DB and if it is done at restaurant is immediately sent to kitchen
     * otherwise it is just saved and it will be checked and sent in the right time
     * by the checkOrdersToSend scheduled method
     * */
    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        User orderUser = order.getUser();

        if (order.getOrderType() != OrderTypeEnum.IN_RESTAURANT && orderUser == null) {
            return ResponseEntity.badRequest().build(); // User is mandatory for not in restaurant orders
        }

        if (orderUser != null) { // if the user is registered
            FirebaseToken firebaseToken = checkFirebaseAuth(order.getUser().getUserId());
            if (firebaseToken == null) {
                return ResponseEntity.badRequest().build();
            }
            orderUser.setUserId(firebaseToken.getUid());
            orderUser.setUsername(firebaseToken.getName());
            User savedUser = userRepository.save(orderUser);
            order.setUser(savedUser);
        }

        if (order.getDateTime() == null) {
            order.setDateTime(LocalDateTime.now());
        } else {
            order.setDateTime(order.getDateTime());
        }

        Order savedOrder = orderRepository.save(order);

        // sending single preparations of the order to the kitchen
        for (Preparation preparation : order.getPreparationList()) {
            Preparation newPreparation = new Preparation();
            newPreparation.setOrder(savedOrder);
            newPreparation.setName(preparation.getName());
            newPreparation.setTableNum(preparation.getTableNum());
            newPreparation.setState(PreparationStatesEnum.WAITING);

            Preparation savedPreparation = preparationRepository.save(newPreparation);

            // check if the preparation is ordered at table and is immediately to prepare then send it to the kitchen
            if (order.getOrderType() == OrderTypeEnum.IN_RESTAURANT) {
                rabbitMqSender.send(savedPreparation);
            }
        }

        if (savedOrder.getUser() != null) {
            savedOrder.setOrderName(savedOrder.getUser().getUsername());
        }
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Method used to mark an order (delivery or take away) accepted and eligible to be sent to the kitchen
     * NOTE: Preorder orders should not be accepted here
     *      but in Reservation microservice since are related to a reservation
     * */
    @PutMapping("/accept/{id}")
    public ResponseEntity<Order> acceptOrder(@PathVariable(value = "id") Long orderId) {
        Order order = orderRepository.findDistinctFirstById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setOrderState(OrderStateEnum.ACCEPTED);
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getOrderType() != OrderTypeEnum.IN_RESTAURANT) {
            savedOrder.setOrderName(savedOrder.getUser().getUsername());
        }
        return ResponseEntity.ok(savedOrder);
    }


    /**
     * Method used to mark an order (delivery or take away) rejected and not eligible to be sent to the kitchen
     * */
    @PutMapping("/reject/{id}")
    public ResponseEntity<Order> rejectOrder(@PathVariable(value = "id") Long orderId) {

        Order order = orderRepository.findDistinctFirstById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setOrderState(OrderStateEnum.REJECTED);
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getOrderType() != OrderTypeEnum.IN_RESTAURANT) {
            savedOrder.setOrderName(savedOrder.getUser().getUsername());
        }
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Scheduled method that checks every 10 seconds if there are new orders to be sent to the kitchen
     * An order is sent to the kitchen if the delivery time chosen by the user is less than 1 hour
     * and it's a delivery/preorder/takeaway order. In restaurant orders are immediately managed during creation.
     * */
    @Scheduled(fixedDelay = 10000)
    @Async
    public void checkOrdersToSend() {
        System.out.println("\n\n[SCHEDULE] Checking preparations to send to the kitchen...");

        List<Order> ordersToPrepare = getOrdersToPrepare();

        for (Order order : ordersToPrepare) {
            for (Preparation preparation : order.getPreparationList()) {
                System.out.println("[SCHEDULE] Sending preparation\n" + preparation);
                rabbitMqSender.send(preparation);
            }
        }
    }

    /**
     * Method used to query and get all the orders (except IN_RESTAURANT orders, managed immediately)
     * that are eligible to be sent to the kitchen.
     * It is executed periodically by the checkOrdersToSend scheduled method.
     * */
    private List<Order> getOrdersToPrepare() {
        List<Order> ordersToPrepare = new ArrayList<>();
        ordersToPrepare.addAll(orderRepository.findAllByOrderStateAndOrderTypeAndDateTimeBefore(
            OrderStateEnum.ACCEPTED, OrderTypeEnum.DELIVERY, LocalDateTime.now().plusHours(1)));
        ordersToPrepare.addAll(orderRepository.findAllByOrderStateAndOrderTypeAndDateTimeBefore(
            OrderStateEnum.ACCEPTED, OrderTypeEnum.TAKEAWAY, LocalDateTime.now().plusHours(1)));
        ordersToPrepare.addAll(orderRepository.findAllByOrderStateAndOrderTypeAndDateTimeBefore(
            OrderStateEnum.ACCEPTED, OrderTypeEnum.PREORDER, LocalDateTime.now().plusHours(1)));
        return ordersToPrepare;
    }

    private FirebaseToken checkFirebaseAuth(String idToken) {
        FirebaseToken decodedToken = null;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            return null;
        }
        return decodedToken;
    }


}
