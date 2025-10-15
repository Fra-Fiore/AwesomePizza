package com.francescofiore.awesomepizza.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "DTO returned when reading pizza data")
public class PizzaResponseDTO {

    @Schema(description = "Pizza identifier", example = "1")
    private Long id;

    @Schema(description = "Pizza name", example = "Margherita")
    private String name;

    @Schema(description = "Pizza price", example = "8.50")
    private Double price;

    @Schema(description = "Pizza description", example = "Tomato and mozzarella")
    private String description;

    @Schema(description = "Optional pizza image URL", example = "https://example.com/margherita.jpg")
    private String imageUrl;

    @Schema(description = "List of ingredient names", example = "[\"Pomodoro\", \"Mozzarella\"]")
    private List<String> ingredients;

    @Schema(description = "Whether the pizza is vegan", example = "false")
    private Boolean veganOk;

    @Schema(description = "Whether the pizza is vegetarian", example = "true")
    private Boolean vegetarianOk;

    @Schema(description = "Whether the pizza is gluten-free", example = "false")
    private Boolean glutenFree;
}
