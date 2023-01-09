package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.enums.OrderTypeEnum;
import it.unito.edu.scavolini.order_management.model.Order;
import it.unito.edu.scavolini.order_management.model.Preparation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAll();

    Order findDistinctFirstById(Long id);

    //TODO: if time permits use better technique for query
    List<Order> findAllByOrderStateAndOrderTypeAndDateTimeBefore(
        OrderStateEnum orderState, OrderTypeEnum orderType, LocalDateTime dateTime);
}
