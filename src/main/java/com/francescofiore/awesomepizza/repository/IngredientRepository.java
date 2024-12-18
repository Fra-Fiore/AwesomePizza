package com.francescofiore.awesomepizza.repository;

import com.francescofiore.awesomepizza.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
