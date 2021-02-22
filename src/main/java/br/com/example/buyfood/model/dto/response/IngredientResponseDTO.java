package br.com.example.buyfood.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class IngredientResponseDTO {

    private String ingredient;
    private String portion;
    private int status;
}