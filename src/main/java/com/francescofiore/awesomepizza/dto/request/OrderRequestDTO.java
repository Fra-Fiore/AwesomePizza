package com.francescofiore.awesomepizza.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "DTO per la creazione di un ordine")
public class OrderRequestDTO {

    @NotEmpty(message = "Order must contain at least one pizza.")
    @Schema(description = "Lista di pizze nell'ordine")
    private List<@Valid OrderPizzaDTO> pizzas;

    @Data
    @Schema(description = "Dettagli di una pizza nell'ordine")
    public static class OrderPizzaDTO {

        @NotNull(message = "Pizza ID must not be null.")
        @Min(value = 1, message = "Pizza ID must be greater than or equal to 1.")
        @Schema(description = "ID della pizza", example = "1")
        private Long pizzaId;

        @NotNull(message = "Quantity must not be null.")
        @Min(value = 1, message = "Quantity must be at least 1.")
        @Schema(description = "ID della pizza", example = "1")
        private Integer quantity;

        @Schema(description = "Note opzionali per la pizza", example = "Senza formaggio")
        private String notes;
    }
}