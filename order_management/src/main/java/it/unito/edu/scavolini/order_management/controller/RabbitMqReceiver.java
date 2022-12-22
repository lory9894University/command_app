package it.unito.edu.scavolini.order_management.controller;

import com.google.gson.Gson;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.repository.KitchenRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/***
 * Class used to test TODO delete
 */
@Component
@EnableRabbit
public class RabbitMqReceiver {

    //this is a JSON object mapper from library Jackson Databind, it's used to convert a java object to a json string and viceversa
    Gson gson = new Gson();

    @Autowired
    private KitchenRepository kitchenRepository;

    @RabbitListener(queues = "kitchen")
    public void receiveMessage(@Payload String message) {
    //if we need to do different things with different messages lookup message headers
        System.out.println("Received <" + message + ">");
        //convert the json string to a preparation object, try catch in case of malformed object
        try {
            Preparation preparation = gson.fromJson(message, Preparation.class);
            System.out.println("Preparation:\n <" + preparation + ">");

            Preparation newPreparation = new Preparation(preparation.getName(), preparation.getTableNum(), preparation.getOrder());
            kitchenRepository.save(newPreparation);

         } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

