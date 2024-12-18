package com.francescofiore.awesomepizza.controller;

import com.francescofiore.awesomepizza.dto.request.PizzaRequestDTO;
import com.francescofiore.awesomepizza.dto.response.PizzaResponseDTO;
import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.PizzaNotFoundException;
import com.francescofiore.awesomepizza.mapper.PizzaMapper;
import com.francescofiore.awesomepizza.service.interfaces.PizzaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PizzaController.class)
class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;

    @MockBean
    private PizzaMapper pizzaMapper;

    @Test
    void getAllPizzas() throws Exception {
        Pizza pizza = new Pizza()
                .setPizzaId(1L)
                .setName("Margherita")
                .setPrice(7.99);

        PizzaResponseDTO pizzaResponse = new PizzaResponseDTO()
                .setId(1L)
                .setName("Margherita")
                .setPrice(7.99);

        Mockito.when(pizzaService.getAllPizzas()).thenReturn(List.of(pizza));
        Mockito.when(pizzaMapper.toResponse(pizza)).thenReturn(pizzaResponse);

        mockMvc.perform(get("/pizzas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Margherita"))
                .andExpect(jsonPath("$[0].price").value(7.99));
    }

    @Test
    void getPizzaById() throws Exception {
        Pizza pizza = new Pizza()
                .setPizzaId(1L)
                .setName("Margherita")
                .setPrice(7.99);

        PizzaResponseDTO pizzaResponse = new PizzaResponseDTO()
                .setId(1L)
                .setName("Margherita")
                .setPrice(7.99);

        Mockito.when(pizzaService.getPizzaById(1L)).thenReturn(pizza);
        Mockito.when(pizzaMapper.toResponse(pizza)).thenReturn(pizzaResponse);

        mockMvc.perform(get("/pizzas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Margherita"))
                .andExpect(jsonPath("$.price").value(7.99));
    }

    @Test
    void createPizza() throws Exception {
        PizzaRequestDTO pizzaRequest = new PizzaRequestDTO()
                .setName("Diavola")
                .setPrice(9.99);

        Pizza pizza = new Pizza()
                .setPizzaId(2L)
                .setName("Diavola")
                .setPrice(9.99);

        PizzaResponseDTO pizzaResponse = new PizzaResponseDTO()
                .setId(2L)
                .setName("Diavola")
                .setPrice(9.99);

        Mockito.when(pizzaMapper.toEntity(Mockito.any(PizzaRequestDTO.class))).thenReturn(pizza);
        Mockito.when(pizzaService.createPizza(Mockito.any(Pizza.class))).thenReturn(pizza);
        Mockito.when(pizzaMapper.toResponse(pizza)).thenReturn(pizzaResponse);

        mockMvc.perform(post("/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Diavola",
                                    "price":9.99,
                                    "ingredientIds":[1,2]
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Diavola"))
                .andExpect(jsonPath("$.price").value(9.99));
    }

    @Test
    void updatePizza() throws Exception {
        PizzaRequestDTO pizzaRequest = new PizzaRequestDTO()
                .setName("Capricciosa")
                .setPrice(10.99);

        Pizza updatedPizza = new Pizza()
                .setPizzaId(1L)
                .setName("Capricciosa")
                .setPrice(10.99);

        PizzaResponseDTO pizzaResponse = new PizzaResponseDTO()
                .setId(1L)
                .setName("Capricciosa")
                .setPrice(10.99);

        Mockito.when(pizzaMapper.toEntity(Mockito.any(PizzaRequestDTO.class))).thenReturn(updatedPizza);
        Mockito.when(pizzaService.updatePizza(Mockito.eq(1L), Mockito.any(Pizza.class))).thenReturn(updatedPizza);
        Mockito.when(pizzaMapper.toResponse(updatedPizza)).thenReturn(pizzaResponse);

        mockMvc.perform(put("/pizzas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Diavola",
                                    "price":9.99,
                                    "ingredientIds":[1,2]
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Capricciosa"))
                .andExpect(jsonPath("$.price").value(10.99));
    }

    @Test
    void deletePizza() throws Exception {
        Mockito.doNothing().when(pizzaService).deletePizza(1L);

        mockMvc.perform(delete("/pizzas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testPizzaNotFound() throws Exception {
        Mockito.when(pizzaService.getPizzaById(1L))
                .thenThrow(new PizzaNotFoundException(1L));

        mockMvc.perform(get("/pizzas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pizza with id 1 not found"));
    }
}
