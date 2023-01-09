package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.model.Preparation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PreparationRepository extends CrudRepository<Preparation, Long> {

    List<Preparation> findAll();

    Preparation findDistinctFirstById(Long id);

    Preparation findDistinctFirstByNameAndTableNum(String name, String tableNum);
}
