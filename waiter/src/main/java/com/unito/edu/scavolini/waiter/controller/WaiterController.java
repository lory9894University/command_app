package com.unito.edu.scavolini.waiter.controller;

import com.unito.edu.scavolini.waiter.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.waiter.model.Preparation;
import com.unito.edu.scavolini.waiter.rabbitMq.DeliveredPreparationSender;
import com.unito.edu.scavolini.waiter.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/")
public class WaiterController {

    @Autowired
    private WaiterRepository waiterRepository;

    @Value("${kitchen_url}")
    private String kitchen_url;

    @Autowired
    private DeliveredPreparationSender deliveredPreparationSender;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations() {

        return waiterRepository.findAll();
    }

    @PutMapping("/preparations/state/delivered/{id}")
    public ResponseEntity<Preparation> setDelivered(@PathVariable int id) {
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(id);
        if (preparationToChange == null) {
            return ResponseEntity.notFound().build();
        }

        preparationToChange.setState(PreparationStatesEnum.DELIVERED);
        waiterRepository.delete(preparationToChange);

        deliveredPreparationSender.send(preparationToChange);
        return ResponseEntity.ok(preparationToChange);
    }


    @PostMapping(value = "/preparations/create", consumes = "application/json")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        System.out.println("Received preparation: " + preparation);
        return waiterRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
    }

}
