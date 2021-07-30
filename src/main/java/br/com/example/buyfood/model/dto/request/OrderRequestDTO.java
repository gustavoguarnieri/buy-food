package br.com.example.buyfood.model.dto.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {

  @NotNull private Long establishmentId;
  @NotNull private Long deliveryAddressId;
  private List<OrderItemsRequestDTO> items;
  private Long paymentWayId;
  private PreparationStatusRequestDTO preparationStatus;
  private String observation;
}
