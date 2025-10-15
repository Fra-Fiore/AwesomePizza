package com.francescofiore.awesomepizza.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Schema(description = "DTO returned when reading order data")
public class OrderResponseDTO {

    @Schema(description = "Internal order identifier", example = "123")
    private Long id;

    @Schema(description = "Public order code", example = "ORD000001")
    private String orderCode;

    @Schema(description = "Order creation timestamp", example = "2024-12-14T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Current order status", example = "IN_PREPARATION")
    private String status;

    @Schema(description = "Total amount to pay", example = "15.00")
    private Double totalPrice;

    @Schema(description = "List of pizzas included in the order")
    private List<OrderPizzaResponseDTO> pizzas;

    @Data
    @Schema(description = "Pizza details included in the order response")
    public static class OrderPizzaResponseDTO {

        @Schema(description = "Pizza name", example = "Margherita")
        private String pizzaName;

        @Schema(description = "Ordered quantity", example = "2")
        private Integer quantity;

        @Schema(description = "Optional notes", example = "No cheese")
        private String notes;
    }
}