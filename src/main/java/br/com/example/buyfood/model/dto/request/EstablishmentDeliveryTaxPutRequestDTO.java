package br.com.example.buyfood.model.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstablishmentDeliveryTaxPutRequestDTO {

  @NotNull private BigDecimal taxAmount;
  private int status = 1;
}
