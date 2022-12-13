package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.model.Preparation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;


@Component
public class RabbitMqSender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    public void send(Preparation preparation) {
        this.template.convertAndSend(this.queue.getName(), "Jsonificare la preparation");
        System.out.println(" [x] Sent '" + preparation + "'");
    }
}
