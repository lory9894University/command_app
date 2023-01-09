package it.unito.edu.scavolini.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unito.edu.scavolini.reservation.enums.PreparationStatesEnum;
import it.unito.edu.scavolini.reservation.model.Order;
import it.unito.edu.scavolini.reservation.model.Preparation;
import it.unito.edu.scavolini.reservation.repository.OrderRepository;
import it.unito.edu.scavolini.reservation.repository.PreparationRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableRabbit
public class RabbitMqReceiver {

    @Autowired
    PreparationRepository preparationRepository;

    @Autowired
    OrderRepository orderRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * TODO: remove, used to test
     * */
    @RabbitListener(queues = "preorder")
    public void receivePreorder(@Payload String message) {
        System.out.println("\n\nReceived <" + message + ">");

        objectMapper.registerModule(new JavaTimeModule());

        try {
            Order order = objectMapper.readValue(message, Order.class);
            order.setId(null);
            System.out.println("\nOrder:\n <" + order + ">");

            order.setTableNum("RIC_" + order.getTableNum());

            Order savedOrder = orderRepository.save(order);

            for (Preparation preparation : order.getPreparationList()) {
                Preparation newPreparation = new Preparation();
                newPreparation.setOrder(savedOrder);
                newPreparation.setName("RIC_" + preparation.getName());
                newPreparation.setTableNum(preparation.getTableNum());
                newPreparation.setState(PreparationStatesEnum.WAITING);

                Preparation savedPreparation = preparationRepository.save(newPreparation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

