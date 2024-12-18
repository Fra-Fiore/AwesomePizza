package com.francescofiore.awesomepizza.repository;

import com.francescofiore.awesomepizza.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}
