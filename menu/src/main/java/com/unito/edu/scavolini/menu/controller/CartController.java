package com.unito.edu.scavolini.menu.controller;


import com.unito.edu.scavolini.menu.model.Cart;
import com.unito.edu.scavolini.menu.model.CartEntry;
import com.unito.edu.scavolini.menu.model.Dish;
import com.unito.edu.scavolini.menu.model.UserSession;
import com.unito.edu.scavolini.menu.model.idClasses.CartEntryId;
import com.unito.edu.scavolini.menu.repository.CartEntryRepository;
import com.unito.edu.scavolini.menu.repository.CartRepository;
import com.unito.edu.scavolini.menu.repository.DishRepository;
import com.unito.edu.scavolini.menu.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @GetMapping("/getAllCarts")
    public List<Cart> getAllCarts() {
        return (List<Cart>) cartRepository.findAll();
    }

    @GetMapping("/getCartById")
    public Cart getCartById(@RequestBody CartId cartId) {

        return cartRepository.findDistinctFirstById(cartId.id);
    }

    @GetMapping("/getCartByUserSessionId")
    public Cart getCartByUserSessionId(@RequestParam Long id) {
        return cartRepository.findDistinctFirstByUserSessionId(id);
    }

    @PostMapping("/createCart")
    public Cart postCart(@RequestBody Cart cart) {
        UserSession u = cart.getUserSession();
        boolean userSessionExists = userSessionRepository.existsById(u.getId());
        if(!userSessionExists) {
            // user session does not exist, return error
            return null;
        }
        return cartRepository.save(cart);
    }

    @PostMapping("/deleteCart")
    public void deleteCart(@RequestBody CartId cartId) {
        boolean exists = cartRepository.existsById(cartId.id);
        if (exists) {
            cartRepository.deleteById(cartId.id);
        }
    }

    @PostMapping("/addDishToCart")
    public CartEntry addDishToCart(@RequestBody CartDish cartDish) {
        CartEntry cartEntry = getCartEntryIfCartAndDishExist(cartDish.cartId, cartDish.dishId, true);
        // if the dish is already in the cart, increase the quantity
        if (cartEntry != null) {
            cartEntry.setQuantity(cartEntry.getQuantity() + 1);
            return cartEntryRepository.save(cartEntry);
        } else {
            return null;
        }
    }

    @PostMapping("/removeDishFromCart")
    public CartEntry removeDishFromCart(@RequestBody CartDish cartDish) {
        CartEntry cartEntry = getCartEntryIfCartAndDishExist(cartDish.cartId, cartDish.dishId);
        if (cartEntry != null) {
            cartEntry.setQuantity(cartEntry.getQuantity() - 1);
            if(cartEntry.getQuantity() == 0) {
                // if the quantity is 0, delete the cart entry
                cartEntryRepository.delete(cartEntry);
            } else {
                // otherwise, decrease the quantity by 1
                cartEntryRepository.save(cartEntry);
            }
            return cartEntry;
        } else {
            return null;
        }
    }


    @GetMapping("/getCartEntriesByCartId")
    public List<CartEntry> getCartEntriesByCartId(@RequestBody CartId cartId) {
        return cartEntryRepository.findAllByCartId(cartId.id);
    }

    /**
     * Returns cart entry if cart exists, dish exists and cart entry exists. Otherwise, returns null.
     * @param cartId cart id
     * @param dishId dish id
     * @return cart entry
     */
    private CartEntry getCartEntryIfCartAndDishExist(Long cartId, Long dishId, boolean createIfNotExists) {
        boolean cartExists = cartRepository.existsById(cartId);
        boolean dishExists = dishRepository.existsById(dishId);
        if(!cartExists||!dishExists) {
            return null;
        }
        Cart cart = cartRepository.findDistinctFirstById(cartId);
        Dish dish = dishRepository.findDistinctFirstById(dishId);
        CartEntryId id = new CartEntryId(cart, dish);
        boolean cartEntryExists = cartEntryRepository.existsById(id);
        if(!cartEntryExists) {
            if(createIfNotExists) {
                CartEntry cartEntry = new CartEntry(cart, dish, 0);
                return cartEntryRepository.save(cartEntry);
            } else {
                return null;
            }
        }else {
            return cartEntryRepository.findDistinctFirstByCartIdAndDishId(cartId, dishId);
        }

    }

    private CartEntry getCartEntryIfCartAndDishExist(Long cartId, Long dishId) {
        return getCartEntryIfCartAndDishExist(cartId, dishId, false);
    }


    private record CartDish(Long cartId, Long dishId) {
    }
    private record CartId(Long id) {}

}
