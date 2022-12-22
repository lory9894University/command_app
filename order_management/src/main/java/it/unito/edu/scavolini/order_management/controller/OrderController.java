package it.unito.edu.scavolini.order_management.controller;

import com.google.gson.Gson;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/kitchen")
public class OrderController {

    // used Gson intead of Jackson as library because it's simpler for json-java conversion
    Gson gson = new Gson();

    @Autowired
    private OrderRepository orderRepository;

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

    @PostMapping("/order/remove")
    public void removeOrder(@RequestBody Order order) {
        Order orderToRemove = orderRepository.findDistinctFirstById(order.getId());
        orderRepository.delete(orderToRemove);
    }

    @PostMapping(value = "/order/create", consumes = "application/json")
    public Order createOrder(@RequestBody Order order) {
//        Order newOrder = gson.fromJson(order, Order.class);

//        System.out.println("Order:\n <" + newOrder + ">");
        Order newOrder = new Order();
        newOrder.setTableNum(order.getTableNum());
        newOrder.setPaymentType(order.getPaymentType());
        newOrder.setState(order.getState());
        newOrder.setTotal(order.getTotal());

        Order savedOrder = orderRepository.save(newOrder);

        // sending single preparations of the order to the kitchen
        for (Preparation preparation : order.getPreparationList()) {
            preparation.setOrder(savedOrder);
            rabbitMqSender.send(preparation);
        }

        return orderRepository.save(newOrder);
    }

}
