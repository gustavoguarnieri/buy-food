package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull
    private Long establishmentId;
    @NotNull
    private Long deliveryAddressId;
    private List<OrderItemsRequestDTO> items;
    @NotBlank
    private String paymentWay;
    private PreparationStatusRequestDTO preparationStatus;
    private String observation;
}