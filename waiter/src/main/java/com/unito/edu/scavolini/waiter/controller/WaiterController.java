package com.unito.edu.scavolini.waiter.controller;

import com.unito.edu.scavolini.waiter.enums.PreparationStatesEnum;
import com.unito.edu.scavolini.waiter.model.Preparation;
import com.unito.edu.scavolini.waiter.rabbitMq.DeliveredPreparationSender;
import com.unito.edu.scavolini.waiter.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/waiter")
public class WaiterController {

    DeliveredPreparationSender deliveredPreparationSender = new DeliveredPreparationSender();


    @Autowired
    private WaiterRepository waiterRepository;
    @Value("${kitchen_microservice_url}")
    private String waiter_microservice_url;

    @GetMapping("/preparations")
    public List<Preparation> getAllPreparations() {

        return waiterRepository.findAll();
    }

    @PostMapping("/preparation/changeState")
    public Preparation changeState(@RequestBody Preparation preparation) {
        Preparation preparationToChange = waiterRepository.findDistinctFirstById(preparation.getId());
        preparationToChange.setState(preparation.getState());
        waiterRepository.delete(preparationToChange);

        try {
            //convert the preparation in JSON format
            //TODO: VAFFANCULO, IL JSON PARSATO A MANO È UNA MERDA, daltro canto bisogrebbe passare a jacson la preparazione senza state e non so farlo
            String jsonPreparation = "{" +
                    "\"name\":\"" + preparationToChange.getName() + "\"," +
                    "\"table\":\"" + preparationToChange.getTable() + "\"" +
                    "}";


            System.out.println(" [x] Sending '" + jsonPreparation + "'");

            //send it via post request to the waiter microservice
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI("http://" + waiter_microservice_url + "/kitchen/preparation/remove");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(jsonPreparation, headers);
            restTemplate.postForEntity(uri, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

         if (preparationToChange.getState() == PreparationStatesEnum.DELIVERED){
            // if preparation gets state delivered send message to queue
            deliveredPreparationSender.send(preparationToChange);
        }

        return preparationToChange;
    }



    @PostMapping(value = "/preparation/create")
    public Preparation postPreparation(@RequestBody Preparation preparation) {

        return waiterRepository.save(new Preparation(preparation.getName(), preparation.getTable()));
    }

}
