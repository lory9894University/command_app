package com.unito.edu.scavolini.waiter;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class WaiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaiterApplication.class, args);
    }

}
