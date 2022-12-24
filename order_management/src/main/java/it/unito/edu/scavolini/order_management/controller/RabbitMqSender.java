package it.unito.edu.scavolini.order_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.repository.OrderRepository;
import it.unito.edu.scavolini.order_management.repository.PreparationRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RabbitMqSender {

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final Queue kitchenQueue;

    @Autowired
    private final Queue deliveredPreparationsQueue;

    @Autowired
    private OrderRepository orderRepository;

    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue kitchenQueue, Queue deliveredPreparationsQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.kitchenQueue = kitchenQueue;
        this.deliveredPreparationsQueue = deliveredPreparationsQueue;
    }

    public void send(Preparation preparation) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonPreparation = null;
        try {
            jsonPreparation = objectMapper.writeValueAsString(preparation);
            this.rabbitTemplate.convertAndSend(this.kitchenQueue.getName(), jsonPreparation);

            // Order sent to kitchen, update order state to not take it again and resend preparations
            Order order = preparation.getOrder();
            order.setOrderState(OrderStateEnum.SENT_TO_KITCHEN);
            orderRepository.save(order);

            System.out.println("\n\n[S] Sent '" + preparation + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}