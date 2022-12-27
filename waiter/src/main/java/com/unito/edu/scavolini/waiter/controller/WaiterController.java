package com.unito.edu.scavolini.waiter.controller;

import com.unito.edu.scavolini.waiter.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.waiter.model.Preparation;
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
    @Value("${api_gateway}")
    private String api_gateway;

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

        RestTemplate restTemplate = new RestTemplate();
        try {
            // Send the deletion request to kitchen as DELETE
             restTemplate.delete("http://" + api_gateway + "/preparations/removeByTableAndName?table=" + preparationToChange.getTable() + "&name=" + preparationToChange.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        preparationToChange.setState(PreparationStatesEnum.DELIVERED);
        waiterRepository.delete(preparationToChange);

        return ResponseEntity.ok(preparationToChange);
    }


    @PostMapping(value = "/preparations/create")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        return waiterRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
    }

}
