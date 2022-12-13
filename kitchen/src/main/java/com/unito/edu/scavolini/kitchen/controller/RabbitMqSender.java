package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.model.Preparation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

/***
 * This class is used to send a message to the queue on rabbitmq, just uses a template (injected by spring, i don't actually know how it works) and a queue (also injected by spring)
 * The queue is the one that is used to send the message to the queue on rabbitmq, and it's configured in the RabbitMqConfig class
 */
@Component
public class RabbitMqSender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    /***
     * Send a preparation to the queue on rabbitmq
     * @param preparation the preparation that is sent to the queue
     */
    public void send(Preparation preparation) {
        this.template.convertAndSend(this.queue.getName(), "Jsonificare la preparation");
        System.out.println(" [x] Sent '" + preparation + "'");
    }
}
