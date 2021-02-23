package br.com.example.buyfood.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {

    private Long id;
    private Long establishmentId;
    private Long deliveryAddressId;
    private List<OrderItemsResponseDTO> items;
    private String paymentWay;
    private String paymentStatus;
    private String preparationStatus;
    private String observation;
    private int status;
}