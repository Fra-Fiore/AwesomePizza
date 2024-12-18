package com.francescofiore.awesomepizza.mapper;

import com.francescofiore.awesomepizza.dto.request.PizzaRequestDTO;
import com.francescofiore.awesomepizza.dto.response.PizzaResponseDTO;
import com.francescofiore.awesomepizza.entity.Ingredient;
import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.IngredientNotFoundException;
import com.francescofiore.awesomepizza.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PizzaMapper {

    private final IngredientRepository ingredientRepository;

    public Pizza toEntity(PizzaRequestDTO request) {
        Pizza pizza = new Pizza()
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setDescription(request.getDescription())
                .setImageUrl(request.getImageUrl())
                .setVeganOk(request.getVeganOk())
                .setVegetarianOk(request.getVegetarianOk())
                .setGlutenFree(request.getGlutenFree());

        if (request.getIngredientIds() != null) {
            pizza.setIngredients(
                    request.getIngredientIds().stream()
                            .map(id -> ingredientRepository.findById(id)
                                    .orElseThrow(() -> new IngredientNotFoundException(id)))
                            .collect(Collectors.toSet())
            );
        }

        return pizza;
    }

    public PizzaResponseDTO toResponse(Pizza pizza) {
        return new PizzaResponseDTO()
                .setId(pizza.getPizzaId())
                .setName(pizza.getName())
                .setPrice(pizza.getPrice())
                .setDescription(pizza.getDescription())
                .setImageUrl(pizza.getImageUrl())
                .setVeganOk(pizza.isVeganOk())
                .setVegetarianOk(pizza.isVegetarianOk())
                .setGlutenFree(pizza.isGlutenFree())
                .setIngredients(
                        pizza.getIngredients().stream()
                                .map(Ingredient::getName)
                                .toList()
                );
    }
}
