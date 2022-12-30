package com.unito.edu.scavolini.menu.model;

import com.unito.edu.scavolini.menu.model.idClasses.CartEntryId;
import jakarta.persistence.*;

/**
 * this class represents a cart entry, which is composed by a dish and a quantity associated to a cart
 */
@Table(name = "cart_entry")
@Entity
@IdClass(CartEntryId.class)
public class CartEntry {
    @Id
    @ManyToOne
    private Cart cart;

    @Id
    @ManyToOne
    private Dish dish;

    @Column(name = "quantity", nullable = false)
    private float quantity;

    public CartEntry() {
    }

    public CartEntry(Cart cart, Dish dish, float quantity) {
        this.cart = cart;
        this.dish = dish;
        this.quantity = quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getQuantity() {
        return quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public Dish getDish() {
        return dish;
    }
}

