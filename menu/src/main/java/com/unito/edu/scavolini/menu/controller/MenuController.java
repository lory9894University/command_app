package com.unito.edu.scavolini.menu.controller;

import com.unito.edu.scavolini.menu.repository.DishRepository;
import com.unito.edu.scavolini.menu.exeptions.InvalidDishException;
import com.unito.edu.scavolini.menu.model.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    MenuController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    //region Repositories
    private final DishRepository dishRepository;
    //endregion

    //region API MenuController
    /**
     * This method is used to get all the dishes in the menu.
     *
     * @return a list of all the dishes in the menu.
     */
    @GetMapping("/getDishes")
    public List<Dish> getAllDishes() {

        return dishRepository.findAll();
    }

    @PostMapping(value = "/addDish")
    public ResponseEntity<Dish> postDish(@RequestBody Dish dish) throws InvalidDishException {
        checkDishIsValid(dish);
        Dish savedDish = dishRepository.save(dish);
        return ResponseEntity.ok(savedDish);
    }

    @PostMapping(value = "/addDishes")
    public ResponseEntity<List<Dish>> postDishes(@RequestBody List<Dish> dishes) throws InvalidDishException {
        for (Dish dish : dishes) {
            checkDishIsValid(dish);
        }
        List<Dish> savedDishes = (List<Dish>) dishRepository.saveAll(dishes);
        return ResponseEntity.ok(savedDishes);
    }

    @PostMapping(value = "/deleteDish")
    public ResponseEntity<Dish> deleteDish(@RequestBody DishId dishId) {
        Dish dishToDelete = dishRepository.findDistinctFirstById(dishId.id);
        dishRepository.delete(dishToDelete);
        return ResponseEntity.ok(dishToDelete);
    }

    /** For each field in the dishUpdate object, if it is not null, update the corresponding field in the dishToUpdate
     * object
     * @param dishUpdate the dishUpdate object containing the dish id and the values of the fields to update
     * @return the updated dish
     */
    @PostMapping(value = "/updateDish")
    public ResponseEntity<Dish> updateDish(@RequestBody DishUpdate dishUpdate) {
        Dish dishToUpdate = dishRepository.findDistinctFirstById(dishUpdate.id);

        if(dishToUpdate == null) {
            // dish does not exist, return error
            return ResponseEntity.badRequest().build();
        }

        // update the fields of the dish
        if(dishUpdate.name != null) {
            dishToUpdate.setName(dishUpdate.name);
        }
        if(dishUpdate.price != null) {
            dishToUpdate.setPrice(dishUpdate.price);
        }
        if(dishUpdate.description != null) {
            dishToUpdate.setDescription(dishUpdate.description);
        }
        if(dishUpdate.imgUrl != null) {
            dishToUpdate.setImgURL(dishUpdate.imgUrl);
        }
        if(dishUpdate.course != null) {
            dishToUpdate.setCourse(dishUpdate.course);
        }

        Dish updatedDish = dishRepository.save(dishToUpdate);
        return ResponseEntity.ok(updatedDish);
    }
    //endregion

    //region private methods

    private void checkDishIsValid(Dish dish) throws InvalidDishException {
        if (dish == null){
            throw new InvalidDishException("Dish cannot be null", null);
        }
        if (dish.getName() == null || dish.getName().isEmpty()) {
            throw new InvalidDishException("Dish name cannot be null or empty", dish);
        }
        if (dish.getPrice() == null || dish.getPrice() < 0) {
            throw new InvalidDishException("Dish price cannot be null or negative", dish);
        }
        if (dish.getDescription() == null || dish.getDescription().isEmpty()) {
            throw new InvalidDishException("Dish description cannot be null or empty", dish);
        }
        if (dish.getCourse() == null || dish.getCourse().isEmpty()) {
            throw new InvalidDishException("Dish course cannot be null or empty", dish);
        }
    }

    //endregion

    //region Record classes
    /** This record is used to update a dish in the menu.
     * It contains the id of the dish to update and the new values for the dish.
     *
     * @param id the id of the dish to update.
     * @param name the new name of the dish.
     * @param description the new description of the dish.
     * @param price the new price of the dish.
     * @param course the new course of the dish.
     * @param imgUrl the new image url of the dish.
     */
    private record DishUpdate(
            Long id,
            String name,
            String description,
            Double price,
            String course,
            String imgUrl
    ) {
    }

    private record DishId(Long id) {}
    //endregion
}
