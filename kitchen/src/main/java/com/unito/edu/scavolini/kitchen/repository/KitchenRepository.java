package com.unito.edu.scavolini.kitchen.repository;

import com.unito.edu.scavolini.kitchen.model.Preparation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KitchenRepository extends CrudRepository<Preparation, Long> {

    List<Preparation> findAll();

    Preparation findDistinctFirstById(int id);

    Preparation findDistinctFirstByNameAndTableNum(String name, String table);
}
