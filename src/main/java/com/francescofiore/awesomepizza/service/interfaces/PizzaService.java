package com.francescofiore.awesomepizza.service.interfaces;

import com.francescofiore.awesomepizza.entity.Pizza;

import java.util.List;

public interface PizzaService {

    List<Pizza> getAllPizzas();

    Pizza getPizzaById(Long id);

    Pizza createPizza(Pizza pizza);

    Pizza updatePizza(Long id, Pizza updatedPizza);

    void deletePizza(Long id);

}