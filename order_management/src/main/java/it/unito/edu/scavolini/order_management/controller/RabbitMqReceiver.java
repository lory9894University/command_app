package it.unito.edu.scavolini.order_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unito.edu.scavolini.order_management.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import it.unito.edu.scavolini.order_management.repository.OrderRepository;
import it.unito.edu.scavolini.order_management.repository.PreparationRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@EnableRabbit
public class RabbitMqReceiver {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Method to add to kitchen microservice.
     * Used to receive the preparation sent from order management microservice.
     * */
    @RabbitListener(queues = "kitchen")
    public void receiveMessageKitchen(@Payload String message) {
        System.out.println("\n\n[R] Received <" + message + ">");

        objectMapper.registerModule(new JavaTimeModule());

        try {
            Preparation preparation = objectMapper.readValue(message, Preparation.class);
            System.out.println("Preparation:\n <" + preparation + ">");

            // reset id in order to add a new entry, otherwise it could overwrite one already present
            preparation.setId(null);

            //preparationRepository.save(newPreparation);

         } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive message from waiter on deliveredPreparation queue to mark
     * the preparation as delivered and update it in the DB
     *
     * NOTE: should not still be deleted, cause it could still be needed for the payment
     * */
    @RabbitListener(queues = "deliveredPreparations")
    public void markDeliveredPreparation(@Payload String message) {
        System.out.println("\n\n[R] Received <" + message + ">");

        try {
            Preparation preparation = objectMapper.readValue(message, Preparation.class);
            System.out.println("\nPreparation:\n <" + preparation + ">");

            Preparation dbPreparation = preparationRepository.findDistinctFirstByNameAndTableNum(
                preparation.getName(), preparation.getTableNum());

            dbPreparation.setState(PreparationStatesEnum.DELIVERED);
            preparationRepository.save(dbPreparation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive message from reservation microservice with orders related to reservations (preorders)
     * */
    @RabbitListener(queues = "preorder")
    public void receivePreorder(@Payload String message) {
        System.out.println("\n\nReceived <" + message + ">");

        objectMapper.registerModule(new JavaTimeModule());

        try {
            Order order = objectMapper.readValue(message, Order.class);

            // reset id in order to add a new entry, otherwise it could overwrite one already present
            order.setId(null);
            System.out.println("\nOrder:\n <" + order + ">");

            Order savedOrder = orderRepository.save(order);

            for (Preparation preparation : order.getPreparationList()) {
                Preparation newPreparation = new Preparation();
                newPreparation.setOrder(savedOrder);
                newPreparation.setName(preparation.getName());
                newPreparation.setTableNum(preparation.getTableNum());
                newPreparation.setState(PreparationStatesEnum.WAITING);

                Preparation savedPreparation = preparationRepository.save(newPreparation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

