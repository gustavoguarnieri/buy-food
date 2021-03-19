package br.com.example.buyfood.model.dto.response;

import br.com.example.buyfood.enums.EstablishmentCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstablishmentResponseDTO {

    private Long id;
    private String companyName;
    private String tradingName;
    private String email;
    private String commercialPhone;
    private String mobilePhone;
    private EstablishmentCategory category;
    private BusinessHoursResponseDTO businessHours;
    private DeliveryTaxResponseDTO deliveryTax;
    private int status;
}
