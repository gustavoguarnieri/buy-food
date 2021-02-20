package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer status = 1;
}