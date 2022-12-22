package com.unito.edu.scavolini.waiter.controller;

import com.unito.edu.scavolini.waiter.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.waiter.model.Preparation;
import com.unito.edu.scavolini.waiter.repository.WaiterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.unito.edu.scavolini.waiter.rabbitMq.DeliveredPreparationSender;
import java.util.List;

@RestController
@RequestMapping("/api/waiter")
public class WaiterController {

    DeliveredPreparationSender deliveredPreparationSender = new DeliveredPreparationSender();

    @Autowired
    private WaiterRepository waiterRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations() {

        return waiterRepository.findAll();
    }

    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestBody PreparationChangeStateBody prepChange) {
        // TODO maybe change state can be simplified to accept only delivered as newState
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(prepChange.preparationId());
        preparationToChange.setState(prepChange.newState());
        Preparation preparationSaved = waiterRepository.save(preparationToChange);
        if (preparationSaved.getState() == PreparationStatesEnum.DELIVERED){
            // if preparation gets state delivered send message to queue
            deliveredPreparationSender.send(preparationSaved);
        }

        return waiterRepository.save(preparationToChange);
    }

    @PostMapping(value = "/preparation/create", consumes = "application/json")
    public Preparation postPreparation(@RequestBody NewPreparationBody preparationBody) {

        return waiterRepository.save(new Preparation(preparationBody.name(), preparationBody.tableNum()));
    }

}

/**
 * This class is used to parse the body of the POST request of the creation of a new preparation
 */
record NewPreparationBody(String name, String tableNum) {
}

/**
 * This class is used to parse the body of the POST request of the change of state of a preparation
 */
record PreparationChangeStateBody(int preparationId, PreparationStatesEnum newState) {
}
