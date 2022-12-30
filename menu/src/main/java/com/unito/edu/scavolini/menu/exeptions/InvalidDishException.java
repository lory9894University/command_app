package com.unito.edu.scavolini.menu.exeptions;

import com.unito.edu.scavolini.menu.model.Dish;

public class InvalidDishException extends Exception {
    private final Dish dish;
    public InvalidDishException(String message, Dish dish) {
        super(message);
        this.dish = dish;
    }

    public Dish getDish() {
        return dish;
    }
}
