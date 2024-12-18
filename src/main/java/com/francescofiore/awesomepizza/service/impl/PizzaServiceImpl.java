package com.francescofiore.awesomepizza.service.impl;

import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.PizzaNotFoundException;
import com.francescofiore.awesomepizza.repository.PizzaRepository;
import com.francescofiore.awesomepizza.service.interfaces.PizzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;

    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    public Pizza getPizzaById(Long id) {
        return pizzaRepository.findById(id)
                .orElseThrow(() -> new PizzaNotFoundException(id));
    }

    public Pizza createPizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    public Pizza updatePizza(Long id, Pizza updatedPizza) {
        Pizza pizza = getPizzaById(id)
                .setName(updatedPizza.getName())
                .setPrice(updatedPizza.getPrice())
                .setIngredients(updatedPizza.getIngredients());
        return pizzaRepository.save(pizza);
    }

    public void deletePizza(Long id) {
        var pizza = getPizzaById(id);
        pizzaRepository.delete(pizza);
    }

}