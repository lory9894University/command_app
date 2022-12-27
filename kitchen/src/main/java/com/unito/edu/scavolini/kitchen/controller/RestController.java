package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import java.util.List;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/")
public class RestController {

    @Autowired
    private KitchenRepository kitchenRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations(){

        return kitchenRepository.findAll();
    }

    /**
     * This method is used to get a preparation ID by name and table number
     * @param table the table number
     * @param name the name of the preparation
     * @return Preparation ID
     */
    @GetMapping("/preparations/getId")
    public ResponseEntity<Integer> getPreparationId(@RequestParam(value = "table") String table, @RequestParam(value = "name") String name){
        Preparation preparation = kitchenRepository.findDistinctFirstByNameAndTableNum(name, table);
        return preparation != null ? ResponseEntity.ok(preparation.getId()) : ResponseEntity.notFound().build();
    }


    @Value("${api_gateway}")
    private String api_gateway;

    @PutMapping("/preparations/state/waiting/{id}")
    public ResponseEntity<Preparation> setStateWaiting(@PathVariable int id){
        Preparation preparation = kitchenRepository.findDistinctFirstById(id);
        if (preparation == null){
            return ResponseEntity.notFound().build();
        }
        preparation.setState(PreparationStatesEnum.WAITING);
        kitchenRepository.save(preparation);
        return ResponseEntity.ok(preparation);
    }

    @PutMapping("/preparations/state/uderway/{id}")
    public ResponseEntity<Preparation> setStateUnderway(@PathVariable int id){
        Preparation preparation = kitchenRepository.findDistinctFirstById(id);
        if (preparation == null){
            return ResponseEntity.notFound().build();
        }
        preparation.setState(PreparationStatesEnum.UNDERWAY);
        kitchenRepository.save(preparation);
        return ResponseEntity.ok(preparation);
    }

    @PutMapping("/preparations/state/ready/{id}")
    public ResponseEntity<Preparation> setStateReady(@PathVariable int id){
        ResponseEntity<Preparation> response;
        Preparation preparation = kitchenRepository.findDistinctFirstById(id);
        if (preparation == null){
            return ResponseEntity.notFound().build();
        }

        preparation.setState(PreparationStatesEnum.READY);
        kitchenRepository.save(preparation);


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject preparationJsonObject = new JSONObject();
        preparationJsonObject.put("name", preparation.getName());
        preparationJsonObject.put("table", preparation.getTable());
        try{
            HttpEntity<String> request = new HttpEntity<>(preparationJsonObject.toString(), headers);
            restTemplate.postForEntity("http://" + api_gateway + "/preparation/create", request, String.class);
            System.out.println(" [x] Sent '" + preparationJsonObject + "'");
            response =  ResponseEntity.ok(preparation);


        }catch (Exception e){
            e.printStackTrace();
            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }

    /***
     * This method is used to remove preparation to the kitchen, when a waiter picks up the order remove the preparation from the kitchen
     */
    @DeleteMapping("/preparations/remove/{id}")
    public void removePreparation(@PathVariable int id) {
        Preparation preparationToRemove = kitchenRepository.findDistinctFirstById(id);
        kitchenRepository.delete(preparationToRemove);
    }

    /***
     * This method is used to add a preparation to the kitchen. shouldn't be used, the kitchen should receive the preparation from rabbitmq
     */
    @PostMapping(value = "/preparations/create" , consumes = "application/json")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        return kitchenRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
    }

}
