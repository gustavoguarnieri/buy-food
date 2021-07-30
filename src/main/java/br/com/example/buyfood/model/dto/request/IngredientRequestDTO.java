package br.com.example.buyfood.model.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRequestDTO {

  @NotBlank private String ingredient;
  private String portion;
  private int status = 1;
}
