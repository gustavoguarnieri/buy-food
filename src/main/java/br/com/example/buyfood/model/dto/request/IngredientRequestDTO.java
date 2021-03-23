package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class IngredientRequestDTO {

    @NotBlank
    private String ingredient;
    private String portion;
    private int status = 1;
}