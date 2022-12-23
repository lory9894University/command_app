package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAll();

    Order findDistinctFirstById(Long id);

    //TODO fix SQL syntax
    @Query(value = "SELECT * FROM order WHERE (" +
        "order_type = 'DELIVERY' " +
        "OR order_type = 'PREORDER' " +
        "OR order_type = 'TAKEAWAY') " +
        "AND order_state = 'ACCEPTED' " +
        "AND date_time <= NOW() + INTERVAL 1 HOUR", nativeQuery = true)
    List<Order> findOrdersToPrepare();

}
