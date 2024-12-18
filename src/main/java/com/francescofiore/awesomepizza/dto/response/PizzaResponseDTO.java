package com.francescofiore.awesomepizza.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "DTO per la risposta di una pizza")
public class PizzaResponseDTO {

    @Schema(description = "ID della pizza", example = "1")
    private Long id;

    @Schema(description = "Nome della pizza", example = "Margherita")
    private String name;

    @Schema(description = "Prezzo della pizza", example = "8.50")
    private Double price;

    @Schema(description = "Descrizione della pizza", example = "Pomodoro e mozzarella")
    private String description;

    @Schema(description = "URL dell'immagine della pizza", example = "https://example.com/margherita.jpg")
    private String imageUrl;

    @Schema(description = "Lista di nomi degli ingredienti associati", example = "[\"Pomodoro\", \"Mozzarella\"]")
    private List<String> ingredients;

    @Schema(description = "Indica se la pizza è vegana", example = "false")
    private Boolean veganOk;

    @Schema(description = "Indica se la pizza è vegetariana", example = "true")
    private Boolean vegetarianOk;

    @Schema(description = "Indica se la pizza è senza glutine", example = "false")
    private Boolean glutenFree;
}
