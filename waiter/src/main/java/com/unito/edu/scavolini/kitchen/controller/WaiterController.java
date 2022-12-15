package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waiter")
public class WaiterController {

    @Autowired
    private WaiterRepository waiterRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations() {

        return waiterRepository.findAll();
    }

    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestBody Preparation preparation) {
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(preparation.getId());
        preparationToChange.setState(preparation.getState());

        return waiterRepository.save(preparationToChange);
    }

    @PostMapping(value = "/preparation/create")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        return waiterRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
    }

}
