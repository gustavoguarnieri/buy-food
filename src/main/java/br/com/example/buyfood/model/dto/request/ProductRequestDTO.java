package br.com.example.buyfood.model.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {

  @NotBlank private String name;
  @NotNull private BigDecimal price;
  private String description;
  private int status = 1;
}
