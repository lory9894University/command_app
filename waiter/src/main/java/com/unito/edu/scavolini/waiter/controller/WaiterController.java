package com.unito.edu.scavolini.waiter.controller;

import com.unito.edu.scavolini.waiter.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.waiter.model.Preparation;
import com.unito.edu.scavolini.waiter.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Preparation changeState(@RequestParam int preparationId,
                                   @RequestParam PreparationStatesEnum new_state) {
        // TODO maybe change state can be simplified to accept only delivered as new_state
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(preparationId);
        preparationToChange.setState(new_state);
        Preparation preparationSaved = waiterRepository.save(preparationToChange);
        if (preparationSaved.getState() == PreparationStatesEnum.DELIVERED){
            // if preparation gets state delivered send message to queue
            deliveredPreparationSender.send(preparationSaved);
        }

        return waiterRepository.save(preparationToChange);
    }

    @PostMapping(value = "/preparation/create", consumes = "application/json")
    public Preparation postPreparation(@RequestParam String name,
                                       @RequestParam String tableNum) {

        return waiterRepository.save(new Preparation(name, tableNum));
    }

}
