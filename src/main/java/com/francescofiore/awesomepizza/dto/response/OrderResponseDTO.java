package com.francescofiore.awesomepizza.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Schema(description = "DTO per la risposta di un ordine")
public class OrderResponseDTO {

    @Schema(description = "ID tecnico dell'ordine", example = "123")
    private Long id;

    @Schema(description = "Codice dell'ordine", example = "ORD000001")
    private String orderCode;

    @Schema(description = "Timestamp dell'ordine", example = "2024-12-14T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Stato dell'ordine", example = "IN_PREPARATION")
    private String status;

    @Schema(description = "Somma totale da pagare", example = "15.00")
    private Double totalPrice;

    @Schema(description = "Lista delle pizze nell'ordine")
    private List<OrderPizzaResponseDTO> pizzas;

    @Data
    @Schema(description = "Dettagli di una pizza nella risposta di un ordine")
    public static class OrderPizzaResponseDTO {

        @Schema(description = "Nome della pizza", example = "Margherita")
        private String pizzaName;

        @Schema(description = "Quantità ordinata", example = "2")
        private Integer quantity;

        @Schema(description = "Note opzionali", example = "Senza formaggio")
        private String notes;
    }
}