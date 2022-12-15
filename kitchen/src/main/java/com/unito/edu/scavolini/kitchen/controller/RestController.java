package com.unito.edu.scavolini.kitchen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/kitchen")
public class RestController {

    //this is a JSON object mapper from library Jackson Databind, it's used to convert a java object to a json string and viceversa
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KitchenRepository kitchenRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations(){
        List<Preparation> preparationList = kitchenRepository.findAll();

        return preparationList;
    }

    /***
     * Get a preparation and changes its state accordingly. If the state is changed to "READY" the preparation is sent to RabbitMQ
     */
    @Value("${waiter_microservice_url}")
    private String waiter_microservice_url;

    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestBody Preparation preparation) {
        Preparation preparationToChange = kitchenRepository.findDistinctFirstById(preparation.getId());
        preparationToChange.setState(preparation.getState());

        if (preparationToChange.getState() == PreparationStatesEnum.READY) {
            String jsonPreparation = null;
        try {
            //convert the preparation in JSON format
            jsonPreparation = objectMapper.writeValueAsString(preparationToChange);
            //send it via post request to the waiter microservice
            Jsoup.connect("http://"+waiter_microservice_url+"/waiter/preparation/create")
            .method(Connection.Method.POST)
            .header("Content-Type", "application/json")
            .data("json", jsonPreparation)
            .execute();
            System.out.println(" [x] Sent '" + jsonPreparation + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }

        }
        kitchenRepository.save(preparationToChange);

        return preparationToChange;
    }

    /***
     * This method is used to remove preparation to the kitchen, when a waiter picks up the order remove the preparation from the kitchen
     */
    @PostMapping("/preparation/remove")
    public void removePreparation(@RequestBody int preparationId) {
        Preparation preparationToRemove = kitchenRepository.findDistinctFirstById(preparationId);
        kitchenRepository.delete(preparationToRemove);
    }

    /***
     * This method is used to add a preparation to the kitchen. sould'nt be used, the kitchen should receive the preparation from rabbitmq
     */
    @PostMapping(value = "/preparation/create" , consumes = "application/json")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        Preparation _preparation = kitchenRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
        return _preparation;
    }

}
