package br.com.example.buyfood.model.dto.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPutRequestDTO {

  @NotNull private Long establishmentId;
  @NotNull private Long deliveryAddressId;
  private List<OrderItemsPutRequestDTO> items;
  private Long paymentWayId;
  @NotBlank private String paymentStatus;
  private PreparationStatusRequestDTO preparationStatus;
  private String observation;
  private int status = 1;
}
