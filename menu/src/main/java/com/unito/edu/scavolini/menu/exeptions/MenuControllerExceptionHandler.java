package com.unito.edu.scavolini.menu.exeptions;

import com.unito.edu.scavolini.menu.model.Dish;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "com.unito.edu.scavolini.menu.controller")
public class MenuControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // unexpected error
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(500).body("Unexpected error");
    }

    // invalid dish
    @ExceptionHandler(InvalidDishException.class)
    public ResponseEntity<DishErrorMessage> handleException(InvalidDishException e) {
        return ResponseEntity.status(400).body(new DishErrorMessage(e.getMessage(), e.getDish()));
    }

    private static class DishErrorMessage {

        public DishErrorMessage(String message, Dish dish) {
        }
    }
}


