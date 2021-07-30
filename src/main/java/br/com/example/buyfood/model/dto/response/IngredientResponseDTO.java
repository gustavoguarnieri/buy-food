package br.com.example.buyfood.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientResponseDTO {

  private Long id;
  private String ingredient;
  private String portion;
  private int status;
}
