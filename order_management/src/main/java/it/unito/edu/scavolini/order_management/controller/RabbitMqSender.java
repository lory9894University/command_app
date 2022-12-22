package it.unito.edu.scavolini.order_management.controller;

import com.google.gson.Gson;
import it.unito.edu.scavolini.order_management.model.Preparation;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class RabbitMqSender {

    Gson gson = new Gson();

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final Queue queue;

    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public void send(Preparation preparation) {

        //convert the preparation to a json string
        String jsonPreparation = gson.toJson(preparation);
        this.rabbitTemplate.convertAndSend(this.queue.getName(), jsonPreparation);
        System.out.println(" [x] Sent '" + preparation + "'");

    }
}