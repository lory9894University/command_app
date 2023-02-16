package com.unito.edu.scavolini.kitchen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/***
 * This class is used to receive a message from the queue on rabbitmq,it simply subscribes to the kitchen queue and .... TODO
 */
@Component
@EnableRabbit
public class RabbitMqReceiver {

    //this is a JSON object mapper from library Jackson Databind, it's used to convert a java object to a json string and viceversa
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KitchenRepository kitchenRepository;

    @RabbitListener(queues = "kitchen")
        public void receiveMessage(@Payload String message) {
        //if we need to do different things with different messages lookup message headers
            System.out.println("Received <" + message + ">");
            //convert the json string to a preparation object, try catch in case of malformed object
            try {
            Preparation preparation = objectMapper.readValue(message, Preparation.class);
            kitchenRepository.save(new Preparation(preparation.getName(), preparation.getTableNum()));
            //TODO: preparation should not arrive with an id, it should be generated by the database, so I've used our constructor
            //so in the service that requests the preparation to the kitchen, we have to send also the id of the preparation, or parse json by ourselves
             } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

