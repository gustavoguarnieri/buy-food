package br.com.example.buyfood.model.dto.response;

import br.com.example.buyfood.enums.EstablishmentCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String companyName;
    private String tradingName;
    private String email;
    private String commercialPhone;
    private String mobilePhone;
    private EstablishmentCategory category;
}
