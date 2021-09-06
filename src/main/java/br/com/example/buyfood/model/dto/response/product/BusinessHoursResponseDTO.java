package br.com.example.buyfood.model.dto.response.product;

import br.com.example.buyfood.model.dto.request.BusinessHoursRequestDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id"})
public class BusinessHoursResponseDTO extends BusinessHoursRequestDTO {

  private Long id;
}
