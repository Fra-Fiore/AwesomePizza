package com.francescofiore.awesomepizza.controller;

import com.francescofiore.awesomepizza.dto.request.PizzaRequestDTO;
import com.francescofiore.awesomepizza.dto.response.PizzaResponseDTO;
import com.francescofiore.awesomepizza.mapper.PizzaMapper;
import com.francescofiore.awesomepizza.service.interfaces.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pizzas")
@Tag(name = "Pizzas", description = "API for managing the pizza catalog")
public class PizzaController {

    private final PizzaService pizzaService;
    private final PizzaMapper pizzaMapper;

    @Operation(summary = "Retrieve the pizza catalog", description = "Returns the full list of pizzas with their details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List fetched successfully")
    })
    @GetMapping
    public List<PizzaResponseDTO> getAllPizzas() {
        return pizzaService.getAllPizzas().stream()
                .map(pizzaMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Retrieve pizza details", description = "Returns the details of a pizza given its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pizza found successfully"),
            @ApiResponse(responseCode = "404", description = "Pizza not found")
    })
    @GetMapping("/{id}")
    public PizzaResponseDTO getPizzaById(@PathVariable @NotNull Long id) {
        return pizzaMapper.toResponse(pizzaService.getPizzaById(id));
    }

    @Operation(summary = "Create a new pizza", description = "Creates a new pizza using the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pizza created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<PizzaResponseDTO> createPizza(@Valid @RequestBody PizzaRequestDTO request) {
        PizzaResponseDTO response = pizzaMapper.toResponse(pizzaService.createPizza(pizzaMapper.toEntity(request)));
        return ResponseEntity.created(URI.create("/pizzas/" + response.getId())).body(response);
    }

    @Operation(summary = "Update an existing pizza", description = "Updates the details of a pizza given its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pizza updated successfully"),
            @ApiResponse(responseCode = "404", description = "Pizza not found")
    })
    @PutMapping("/{id}")
    public PizzaResponseDTO updatePizza(@PathVariable @NotNull Long id, @Valid @RequestBody PizzaRequestDTO request) {
        return pizzaMapper.toResponse(pizzaService.updatePizza(id, pizzaMapper.toEntity(request)));
    }

    @Operation(summary = "Delete a pizza", description = "Removes a pizza given its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pizza deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Pizza not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePizza(@PathVariable @NotNull Long id) {
        pizzaService.deletePizza(id);
        return ResponseEntity.noContent().build();
    }
}