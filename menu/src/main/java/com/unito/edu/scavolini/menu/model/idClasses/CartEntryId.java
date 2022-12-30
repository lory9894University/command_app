package com.unito.edu.scavolini.menu.model.idClasses;

import com.unito.edu.scavolini.menu.model.Cart;
import com.unito.edu.scavolini.menu.model.Dish;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * This class is used as a composite key for the CartEntry class.
 */
@Embeddable
public class CartEntryId implements Serializable {
    private Cart cart;
    private Dish dish;

    public CartEntryId() {
    }

    public CartEntryId(Cart cart, Dish dish){
        this.cart = cart;
        this.dish = dish;
    }
}
