package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderPutRequestDto {

    @NotNull
    private Long establishmentId;
    @NotNull
    private Long deliveryAddressId;
    private List<OrderItemsPutRequestDto> items;
    @NotBlank
    private String paymentWay;
    @NotBlank
    private String paymentStatus;
    private String observation;
    private int status = 1;
}