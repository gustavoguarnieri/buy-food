package br.com.example.buyfood.model.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstablishmentDeliveryTaxRequestDTO {

  private Long id;
  @NotNull private BigDecimal taxAmount;
  private int status = 1;
}
