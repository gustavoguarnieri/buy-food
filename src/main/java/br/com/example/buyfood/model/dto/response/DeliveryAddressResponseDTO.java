package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryAddressResponseDTO {

    private Long id;
    private String recipientName;
    private String zipCode;
    private String address;
    private Integer addressNumber;
    private String neighbourhood;
    private String city;
    private String state;
    private String observation;
    private int status;
}