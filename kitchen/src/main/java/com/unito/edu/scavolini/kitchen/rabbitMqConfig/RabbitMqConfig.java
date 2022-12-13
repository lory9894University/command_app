package com.unito.edu.scavolini.kitchen.rabbitMqConfig;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * This class is used to configure the queue on rabbitmq, It creates a bean that is used by the RabbitMqSender class to send a message to the queue
 * The queue "kitchen" this way is also automatically create on RabbitMq.
 */
@EnableRabbit
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue kitchen() {
        return new Queue("kitchen",true);
    }

}