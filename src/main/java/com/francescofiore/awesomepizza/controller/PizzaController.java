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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pizzas")
@Tag(name = "Pizze", description = "API per la gestione del catalogo delle pizze")
public class PizzaController {

    private final PizzaService pizzaService;
    private final PizzaMapper pizzaMapper;

    @Operation(summary = "Visualizza il catalogo delle pizze", description = "Restituisce l'elenco completo delle pizze con i relativi dettagli")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
    })
    @GetMapping
    public List<PizzaResponseDTO> getAllPizzas() {
        return pizzaService.getAllPizzas().stream()
                .map(pizzaMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Visualizza i dettagli di una pizza", description = "Restituisce i dettagli di una pizza dato il suo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pizza trovata con successo"),
            @ApiResponse(responseCode = "404", description = "Pizza non trovata")
    })
    @GetMapping("/{id}")
    public PizzaResponseDTO getPizzaById(@PathVariable @NotNull Long id) {
        return pizzaMapper.toResponse(pizzaService.getPizzaById(id));
    }

    @Operation(summary = "Aggiungi una nuova pizza", description = "Crea una nuova pizza con i dettagli forniti")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pizza creata con successo"),
            @ApiResponse(responseCode = "400", description = "Input non valido")
    })
    @PostMapping
    public PizzaResponseDTO createPizza(@Valid @RequestBody PizzaRequestDTO request) {
        return pizzaMapper.toResponse(pizzaService.createPizza(pizzaMapper.toEntity(request)));
    }

    @Operation(summary = "Aggiorna una pizza esistente", description = "Aggiorna i dettagli di una pizza dato il suo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pizza aggiornata con successo"),
            @ApiResponse(responseCode = "404", description = "Pizza non trovata")
    })
    @PutMapping("/{id}")
    public PizzaResponseDTO updatePizza(@PathVariable @NotNull Long id, @Valid @RequestBody PizzaRequestDTO request) {
        return pizzaMapper.toResponse(pizzaService.updatePizza(id, pizzaMapper.toEntity(request)));
    }

    @Operation(summary = "Elimina una pizza", description = "Rimuove una pizza dato il suo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pizza eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Pizza non trovata")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePizza(@PathVariable @NotNull Long id) {
        pizzaService.deletePizza(id);
        return ResponseEntity.noContent().build();
    }
}