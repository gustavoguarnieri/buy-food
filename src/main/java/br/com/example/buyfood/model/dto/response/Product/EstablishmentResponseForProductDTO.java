package br.com.example.buyfood.model.dto.response.Product;

import br.com.example.buyfood.model.dto.response.EstablishmentCategoryResponseDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentDeliveryTaxResponseDTO;
import br.com.example.buyfood.model.dto.response.UploadFileResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstablishmentResponseForProductDTO {

    private Long id;
    private String companyName;
    private String tradingName;
    private String email;
    private String commercialPhone;
    private String mobilePhone;
    private List<UploadFileResponseDTO> images;
    private EstablishmentCategoryResponseDTO category;
    private BusinessHoursResponseDTO businessHours;
    private EstablishmentDeliveryTaxResponseDTO deliveryTax;
    private int status;
}
