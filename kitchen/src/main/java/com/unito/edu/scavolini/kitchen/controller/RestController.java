package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {

    @Autowired
    private KitchenRepository kitchenRepository;

    @Autowired
    private RabbitMqSender rabbitMqSender;

    @GetMapping("/test")
    public void test() {
        rabbitMqSender.send(new Preparation("test", "T2"));

    }

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations(){
        List<Preparation> preparationList = kitchenRepository.findAll();

        return preparationList;
    }

    /***
     * Get a preparation and changes its state accordingly. If the state is changed to "READY" the preparation is sent to RabbitMQ
     */
    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestBody Preparation preparation) {
        Preparation preparationToChange = kitchenRepository.findDistinctFirstById(preparation.getId());
        preparationToChange.setState(preparation.getState());

        if (preparationToChange.getState() == PreparationStatesEnum.READY) {
            rabbitMqSender.send(preparationToChange);
        }
        kitchenRepository.save(preparationToChange);

        return preparationToChange;
    }

    /***
     * This method is used to remove preparation to the kitchen, when a waiter picks up the order remove the preparation from the kitchen
     */
    @PostMapping("/preparation/remove")
    public void removePreparation(@RequestBody Preparation preparation) {
        Preparation preparationToRemove = kitchenRepository.findDistinctFirstById(preparation.getId());
        kitchenRepository.delete(preparationToRemove);
    }

    @PostMapping(value = "/preparation/create" , consumes = "application/json")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        Preparation _preparation = kitchenRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
        return _preparation;
    }

}