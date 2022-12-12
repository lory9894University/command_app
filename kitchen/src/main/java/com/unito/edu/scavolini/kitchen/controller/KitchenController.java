package com.unito.edu.scavolini.kitchen.controller;

import com.unito.edu.scavolini.kitchen.model.Preparation;
import com.unito.edu.scavolini.kitchen.repository.KitchenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KitchenController {

    @Autowired
    private KitchenRepository kitchenRepository;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations(){
        List<Preparation> preparationList = kitchenRepository.findAll();

        return preparationList;
    }

    @PostMapping(value = "/preparation/create" , consumes = "application/json")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        Preparation _preparation = kitchenRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
        return _preparation;
    }

}
