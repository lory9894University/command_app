package com.unito.edu.scavolini.kitchen.rabbitMqConfig;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("kitchen")
    private String message;

    @Bean
    public Queue kitchen() {
        return new Queue(message,true);
    }

}