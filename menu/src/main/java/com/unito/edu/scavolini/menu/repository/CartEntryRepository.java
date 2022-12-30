package com.unito.edu.scavolini.menu.repository;

import com.unito.edu.scavolini.menu.model.CartEntry;
import com.unito.edu.scavolini.menu.model.idClasses.CartEntryId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartEntryRepository extends CrudRepository<CartEntry, CartEntryId> {
    CartEntry findDistinctFirstByCartIdAndDishId(Long cartId, Long dishId);

    List<CartEntry> findAllByCartId(Long id);

    CartEntry findDistinctFirstByDishId(Long dishId);
}
