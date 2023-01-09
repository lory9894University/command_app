package it.unito.edu.scavolini.reservation.repository;

import it.unito.edu.scavolini.reservation.enums.OrderStateEnum;
import it.unito.edu.scavolini.reservation.enums.OrderTypeEnum;
import it.unito.edu.scavolini.reservation.model.Order;
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
