package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal price;
    private String description;
    private int status = 1;
}