package com.unito.edu.scavolini.kitchen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

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
            //TODO: VAFFANCULO, IL JSON PARSATO A MANO È UNA MERDA, daltro canto bisogrebbe passare a jacson la preparazione senza state e non so farlo
            jsonPreparation = "{" +
                    "\"name\":\"" + preparationToChange.getName() + "\"," +
                    "\"table\":\"" + preparationToChange.getTable() + "\"" +
                    "}";


            System.out.println(" [x] Sending '" + jsonPreparation + "'");

            //send it via post request to the waiter microservice
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI("http://"+ waiter_microservice_url + "/waiter/preparation/create");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
             HttpEntity<String> request = new HttpEntity<String>(jsonPreparation, headers);
            restTemplate.postForEntity(uri, request, String.class);
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
    public void removePreparation(@RequestBody Preparation preparation) {
        Preparation preparationToRemove = kitchenRepository.findDistinctFirstByNameAndTableNum(preparation.getName(), preparation.getTable());
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
