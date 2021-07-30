package br.com.example.buyfood.model.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

  private Long id;
  private EstablishmentResponseDTO establishment;
  private Long deliveryAddressId;
  private EstablishmentDeliveryTaxResponseDTO delivery;
  private List<OrderItemsResponseDTO> items;
  private PaymentWayResponseDTO paymentWay;
  private String paymentStatus;
  private PreparationStatusResponseDTO preparationStatus;
  private String observation;
  private int status;
}
