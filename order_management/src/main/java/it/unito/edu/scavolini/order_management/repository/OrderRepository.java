package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAll();

    Order findDistinctFirstById(int id);

}
