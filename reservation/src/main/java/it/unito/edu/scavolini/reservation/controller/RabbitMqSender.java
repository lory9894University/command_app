package it.unito.edu.scavolini.reservation.controller;

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


    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue preorderQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.preorderQueue = preorderQueue;
    }
}