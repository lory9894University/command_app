package com.unito.edu.scavolini.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


/**
 * Main class of the Menu microservice.
 */
@EntityScan(basePackages = {"com.unito.edu.scavolini.menu.model"})
@SpringBootApplication
public class MenuApplication {

    public static void main(String[] args) {
        SpringApplication.run(MenuApplication.class, args);
    }

}
