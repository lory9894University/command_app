package it.unito.edu.scavolini.reservation.repository;

import it.unito.edu.scavolini.reservation.model.Preparation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PreparationRepository extends CrudRepository<Preparation, Long> {

    List<Preparation> findAll();

    Preparation findDistinctFirstById(Long id);

    Preparation findDistinctFirstByNameAndTableNum(String name, String tableNum);
}
