package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.model.Preparation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/***
 * Class used to test TODO delete
 */
public interface KitchenRepository extends CrudRepository<Preparation, Long> {

    List<Preparation> findAll();

}
