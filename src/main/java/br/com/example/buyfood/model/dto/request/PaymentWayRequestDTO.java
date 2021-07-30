package br.com.example.buyfood.model.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentWayRequestDTO {
  private Long id;
  @NotBlank private String description;
  private int status = 1;
}
