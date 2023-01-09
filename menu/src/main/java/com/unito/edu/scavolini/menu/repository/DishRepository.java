package com.unito.edu.scavolini.menu.repository;

import com.unito.edu.scavolini.menu.model.Dish;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DishRepository extends CrudRepository<Dish, Long> {

    List<Dish> findAll();

    Dish findDistinctFirstById(Long id);

}
