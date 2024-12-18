package com.francescofiore.awesomepizza.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "DTO per la creazione o aggiornamento di una pizza")
public class PizzaRequestDTO {

    @NotEmpty(message = "Pizza name must not be empty.")
    @Size(max = 100, message = "Pizza name must not exceed 100 characters.")
    @Schema(description = "Nome della pizza", example = "Margherita")
    private String name;

    @NotNull(message = "Pizza price must not be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Pizza price must be greater than 0.")
    @Schema(description = "Prezzo della pizza", example = "8.50")
    private Double price;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    @Schema(description = "Descrizione della pizza", example = "Pomodoro e mozzarella")
    private String description;

    @Schema(description = "URL dell'immagine della pizza", example = "https://example.com/margherita.jpg")
    private String imageUrl;

    @NotEmpty(message = "Ingredients list must not be empty.")
    @Schema(description = "Lista di ID degli ingredienti associati", example = "[1, 2]")
    private List<@NotNull(message = "Ingredient ID must not be null.") @Min(value = 1, message = "Ingredient ID must be greater than or equal to 1.") Long> ingredientIds;

    @Schema(description = "Indica se la pizza è vegana", example = "false")
    private Boolean veganOk;

    @Schema(description = "Indica se la pizza è vegetariana", example = "true")
    private Boolean vegetarianOk;

    @Schema(description = "Indica se la pizza è senza glutine", example = "false")
    private Boolean glutenFree;
}
