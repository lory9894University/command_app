package it.unito.edu.scavolini.reservation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unito.edu.scavolini.reservation.model.Order;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSender {

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final Queue preorderQueue;

    private ObjectMapper objectMapper;


    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue preorderQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.preorderQueue = preorderQueue;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void sendPreorder(Order order){
        String jsonOrder;
        try {
            jsonOrder = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.rabbitTemplate.convertAndSend(this.preorderQueue.getName(), jsonOrder);
    }
}