package com.francescofiore.awesomepizza.exception;

public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException(Long ingredientId) {
        super(String.format("Ingredient with id %d not found", ingredientId));
    }
}

