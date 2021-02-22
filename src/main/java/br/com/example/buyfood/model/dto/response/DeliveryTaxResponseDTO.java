package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryTaxResponseDTO {

    private Long id;
    private Long establishmentId;
    private BigDecimal taxAmount;
    private int status;
}