package com.unito.edu.scavolini.menu.repository;

import com.unito.edu.scavolini.menu.model.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {
    Cart findDistinctFirstById(Long id);

    Cart findDistinctFirstByUserSessionId(Long id);
}
