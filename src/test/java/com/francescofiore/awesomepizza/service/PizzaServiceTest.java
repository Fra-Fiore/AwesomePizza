package com.francescofiore.awesomepizza.service;

import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.PizzaNotFoundException;
import com.francescofiore.awesomepizza.repository.PizzaRepository;
import com.francescofiore.awesomepizza.service.impl.PizzaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    @Test
    void getAllPizzas() {
        Pizza pizza1 = new Pizza()
                .setPizzaId(1L)
                .setName("Margherita");

        Pizza pizza2 = new Pizza()
                .setPizzaId(2L)
                .setName("Diavola");

        when(pizzaRepository.findAll()).thenReturn(List.of(pizza1, pizza2));

        List<Pizza> pizzas = pizzaService.getAllPizzas();

        assertEquals(2, pizzas.size());
        assertEquals("Margherita", pizzas.get(0).getName());
        assertEquals("Diavola", pizzas.get(1).getName());
        verify(pizzaRepository, times(1)).findAll();
    }

    @Test
    void getPizzaById_ExistingId() {
        Pizza pizza = new Pizza()
                .setPizzaId(1L)
                .setName("Margherita");

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));

        Pizza foundPizza = pizzaService.getPizzaById(1L);

        assertNotNull(foundPizza);
        assertEquals(1L, foundPizza.getPizzaId());
        assertEquals("Margherita", foundPizza.getName());
        verify(pizzaRepository, times(1)).findById(1L);
    }

    @Test
    void getPizzaById_NonExistingId() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PizzaNotFoundException.class, () -> pizzaService.getPizzaById(1L));
        verify(pizzaRepository, times(1)).findById(1L);
    }

    @Test
    void createPizza() {
        Pizza pizza = new Pizza()
                .setName("Capricciosa")
                .setPrice(10.99);

        when(pizzaRepository.save(pizza)).thenReturn(pizza);

        Pizza createdPizza = pizzaService.createPizza(pizza);

        assertNotNull(createdPizza);
        assertEquals("Capricciosa", createdPizza.getName());
        assertEquals(10.99, createdPizza.getPrice());
        verify(pizzaRepository, times(1)).save(pizza);
    }

    @Test
    void updatePizza_ExistingId() {
        Pizza existingPizza = new Pizza()
                .setPizzaId(1L)
                .setName("Margherita")
                .setPrice(7.99);

        Pizza updatedPizza = new Pizza()
                .setName("Quattro Stagioni")
                .setPrice(9.99);

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(existingPizza));
        when(pizzaRepository.save(existingPizza)).thenReturn(existingPizza);

        Pizza result = pizzaService.updatePizza(1L, updatedPizza);

        assertNotNull(result);
        assertEquals("Quattro Stagioni", result.getName());
        assertEquals(9.99, result.getPrice());
        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, times(1)).save(existingPizza);
    }

    @Test
    void updatePizza_NonExistingId() {
        Pizza updatedPizza = new Pizza()
                .setName("Quattro Stagioni")
                .setPrice(9.99);

        when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PizzaNotFoundException.class, () -> pizzaService.updatePizza(1L, updatedPizza));
        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, never()).save(any(Pizza.class));
    }

    @Test
    void deletePizza_ExistingId() {
        Pizza pizza = new Pizza().setPizzaId(1L);

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
        doNothing().when(pizzaRepository).delete(pizza);

        pizzaService.deletePizza(1L);

        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, times(1)).delete(pizza);
    }

    @Test
    void deletePizza_NonExistingId() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PizzaNotFoundException.class, () -> pizzaService.deletePizza(1L));
        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, never()).delete(any(Pizza.class));
    }
}