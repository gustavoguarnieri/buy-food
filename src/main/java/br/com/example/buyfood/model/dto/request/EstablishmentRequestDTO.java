package br.com.example.buyfood.model.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstablishmentRequestDTO {

  @NotBlank private String companyName;
  @NotBlank private String tradingName;
  @NotBlank @Email private String email;
  private String commercialPhone;
  private String mobilePhone;
  private EstablishmentCategoryRequestDTO category;
  private EstablishmentDeliveryTaxRequestDTO deliveryTax;
  private int status = 1;
}
