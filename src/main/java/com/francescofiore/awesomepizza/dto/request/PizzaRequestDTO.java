package com.francescofiore.awesomepizza.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "DTO used to create or update a pizza")
public class PizzaRequestDTO {

    @NotEmpty(message = "Pizza name must not be empty.")
    @Size(max = 100, message = "Pizza name must not exceed 100 characters.")
    @Schema(description = "Pizza name", example = "Margherita")
    private String name;

    @NotNull(message = "Pizza price must not be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Pizza price must be greater than 0.")
    @Schema(description = "Pizza price", example = "8.50")
    private Double price;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    @Schema(description = "Pizza description", example = "Tomato and mozzarella")
    private String description;

    @Schema(description = "Optional pizza image URL", example = "https://example.com/margherita.jpg")
    private String imageUrl;

    @NotEmpty(message = "Ingredients list must not be empty.")
    @Schema(description = "List of ingredient identifiers", example = "[1, 2]")
    private List<@NotNull(message = "Ingredient ID must not be null.") @Min(value = 1, message = "Ingredient ID must be greater than or equal to 1.") Long> ingredientIds;

    @Schema(description = "Whether the pizza is vegan", example = "false")
    private Boolean veganOk;

    @Schema(description = "Whether the pizza is vegetarian", example = "true")
    private Boolean vegetarianOk;

    @Schema(description = "Whether the pizza is gluten-free", example = "false")
    private Boolean glutenFree;
}
