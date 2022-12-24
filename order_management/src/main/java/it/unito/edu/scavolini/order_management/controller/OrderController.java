package it.unito.edu.scavolini.order_management.controller;

import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.enums.OrderTypeEnum;
import it.unito.edu.scavolini.order_management.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.repository.OrderRepository;
import it.unito.edu.scavolini.order_management.repository.PreparationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private RabbitMqSender rabbitMqSender;

    @GetMapping("/orders")
    public List<Order> getAllOrders(){

        return orderRepository.findAll();
    }

    @Value("${waiter_microservice_url}")
    private String waiter_microservice_url;

    @Value("${kitchen_microservice_url}")
    private String kitchen_microservice_url;

    @GetMapping("/remove/{id}")
    public ResponseEntity<Order> removeOrderById(@RequestParam("id") Long id) {
        Order orderToRemove = orderRepository.findDistinctFirstById(id);
        orderRepository.delete(orderToRemove);
        return ResponseEntity.ok(orderToRemove);
    }

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {

        Order newOrder = new Order();
        newOrder.setTableNum(order.getTableNum());
        newOrder.setPaymentType(order.getPaymentType());
        newOrder.setPaymentState(order.getPaymentState());
        newOrder.setTotal(order.getTotal());
        newOrder.setOrderType(order.getOrderType());
        newOrder.setOrderState(order.getOrderState());

        if (order.getDateTime() == null) {
            newOrder.setDateTime(LocalDateTime.now());
        } else {
            newOrder.setDateTime(order.getDateTime());
        }

        Order savedOrder = orderRepository.save(newOrder);

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

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/order/accept/{id}")
    public ResponseEntity<Order> acceptOrder(@PathVariable(value = "id") Long orderId) {
        Order order = orderRepository.findDistinctFirstById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setOrderState(OrderStateEnum.ACCEPTED);
        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/order/reject/{id}")
    public ResponseEntity<Order> rejectOrder(@PathVariable(value = "id") Long orderId) {
        Order order = orderRepository.findDistinctFirstById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setOrderState(OrderStateEnum.REJECTED);
        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Scheduled method that checks every 10 seconds if there are new orders to send to the kitchen
     * An order is sent to the kitchen if the delivery time chosen by the user is less than 1 hour
     * */
    @Scheduled(fixedDelay = 10000)
    @Async
    public void checkOrdersToSend() {
        System.out.println("[scheduled] Checking orders to send to the kitchen...");
//        List<Order> orders = orderRepository.findAll();
//        for (Order order : orders) {
//            // check if order is a preorder
//            if (order.getOrderType() == OrderTypeEnum.PREORDER && order.getReservation().getDateTime().isBefore(LocalDateTime.now().plusHours(1))
//                || ((order.getOrderType() == OrderTypeEnum.DELIVERY || order.getOrderType() == OrderTypeEnum.TAKEAWAY)
//                    && order.getDateTime().isBefore(LocalDateTime.now().plusHours(1)))
//            ) {
//                if (order.getOrderState() == OrderStateEnum.ACCEPTED) { // TODO: maybe check acceptance from reservation?
//                    for (Preparation preparation : order.getPreparationList()) {
//                        rabbitMqSender.send(preparation);
//                    }
//                }
//            }
//        }
        List<Order> ordersToPrepare = orderRepository.findOrdersToPrepare();
        for (Order order : ordersToPrepare) {
            for (Preparation preparation : order.getPreparationList()) {
                rabbitMqSender.send(preparation);
            }
        }
    }


}
