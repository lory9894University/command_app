package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.enums.PreparationFor;
import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiter")
public class WaiterController {

    @Autowired
    private WaiterRepository waiterRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations(){

        return waiterRepository.findAll();
    }

    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestParam int preparationId,
                                   @RequestParam PreparationStatesEnum new_state) {
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(preparationId);
        preparationToChange.setState(new_state);

        return waiterRepository.save(preparationToChange);
    }

    @PostMapping(value = "/preparation/create" , consumes = "application/json")
    public Preparation postPreparation(@RequestParam String name,
                                       @RequestParam String tableNum) {

        return waiterRepository.save(new Preparation(name, tableNum, PreparationFor.WAITER));
    }

}
