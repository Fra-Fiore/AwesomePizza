package com.francescofiore.awesomepizza.exception;

public class PizzaNotFoundException extends RuntimeException {
    public PizzaNotFoundException(Long pizzaId) {
        super(String.format("Pizza with id %d not found", pizzaId));
    }
}

