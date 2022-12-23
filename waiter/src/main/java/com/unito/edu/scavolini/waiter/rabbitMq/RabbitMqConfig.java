package com.unito.edu.scavolini.waiter.rabbitMq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * This class is used to configure the queue on rabbitmq, It creates a bean that is used by the RabbitMqSender class to send a message to the queue
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue deliveredPreparationsQueue() {
        return new Queue("deliveredPreparations",true);
    }

}
//https://medium.com/javarevisited/first-steps-with-rabbitmq-and-sping-boot-81d293554703