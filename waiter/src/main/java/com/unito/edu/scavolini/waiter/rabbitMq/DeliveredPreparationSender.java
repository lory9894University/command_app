package com.unito.edu.scavolini.waiter.rabbitMq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unito.edu.scavolini.waiter.model.Preparation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

/***
 * This class is used to send a message to the queue on rabbitmq, just uses a template (injected by spring, I don't actually know how it works) and a queue (also injected by spring)
 * The queue is the one that is used to send the message to the queue on rabbitmq, and it's configured in the RabbitMqConfig class
 */
@Component
public class DeliveredPreparationSender {

    //this is a JSON object mapper from library Jackson Databind, it's used to convert a java object to a json string and viceversa
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    /***
     * Send a preparation to the queue on rabbitmq
     * @param preparation the preparation that is sent to the queue
     */
    public void send(Preparation preparation) {
        System.out.println("Sending preparation: " + preparation);

        //convert the preparation to a json string, try catch in case of malformed object
        String jsonPreparation;
        try {
            jsonPreparation = objectMapper.writeValueAsString(preparation);
            this.template.convertAndSend(this.queue.getName(),jsonPreparation );
            System.out.println(" [x] Sent '" + preparation + "'");
        } catch (Exception e) {
            throw new RuntimeException("Sending preparation to queue failed: \n", e);
        }

    }
}