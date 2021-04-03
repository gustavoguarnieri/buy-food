package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PaymentWayRequestDTO {
    private Long id;
    @NotBlank
    private String description;
    private int status = 1;
}