package com.unito.edu.scavolini.waiter.rabbitMqConfig;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * This class is used to configure the queue on rabbitmq, It creates a bean that is used by the RabbitMqSender class to send a message to the queue
 * The queue "waiter" this way is also automatically create on RabbitMq.
 * In this context it shouldn't be necessary to create the configuration for the exchange, since we are just listening to a queue, and not publishing to an exchange.
 * but still if this file is removed the queue will not be created on RabbitMq and if the queue is not created by another service the RabbitMqReceiver will not work.
 */
@Configuration
@EnableRabbit
public class RabbitMqConfig {

    @Bean
    public Queue queue() {
        return new Queue("deliveredPreparations",true);
    }

}
//https://medium.com/javarevisited/first-steps-with-rabbitmq-and-sping-boot-81d293554703