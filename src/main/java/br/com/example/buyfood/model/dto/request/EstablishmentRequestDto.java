package br.com.example.buyfood.model.dto.request;

import br.com.example.buyfood.enums.EstablishmentCategory;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EstablishmentRequestDto {

    @NotBlank
    private String companyName;
    @NotBlank
    private String tradingName;
    @NotBlank
    private String email;
    private String commercialPhone;
    private String mobilePhone;
    private EstablishmentCategory category;
}