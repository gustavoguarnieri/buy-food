package br.com.example.buyfood.model.dto.response;

import br.com.example.buyfood.model.dto.response.product.BusinessHoursResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "establishment"})
public class EstablishmentBusinessHoursResponseDTO extends BusinessHoursResponseDTO {

  private EstablishmentResponseForBusinessHoursDTO establishment;
}
