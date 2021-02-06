package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class DeliveryTaxPutRequestDto {

    @NotNull
    private BigDecimal taxAmount;
    private int status = 1;
}