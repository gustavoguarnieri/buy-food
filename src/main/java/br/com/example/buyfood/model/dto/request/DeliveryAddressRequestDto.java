package br.com.example.buyfood.model.dto.request;

import br.com.example.buyfood.enums.State;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DeliveryAddressRequestDto {

    @NotBlank
    private String recipientName;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String address;
    @NotNull
    private Integer addressNumber;
    @NotBlank
    private String neighbourhood;
    @NotBlank
    private String city;
    private State state;
    private String observation;
    private int status = 1;
}