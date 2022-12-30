package com.unito.edu.scavolini.menu.exeptions;

import com.unito.edu.scavolini.menu.model.Dish;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "com.unito.edu.scavolini.menu.controller")
public class MenuControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // invalid dish
    @ResponseBody
    @ExceptionHandler(InvalidDishException.class)
    protected ResponseEntity<Object> handleInvalidDishException(InvalidDishException e) {
        System.out.println("pippo");
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new DishErrorMessage(e.getMessage(), e.getDish()), HttpStatus.BAD_REQUEST);
    }

    private record DishErrorMessage(String message, Dish dish) {
    }
}


