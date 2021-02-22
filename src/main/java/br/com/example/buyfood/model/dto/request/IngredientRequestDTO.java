package br.com.example.buyfood.model.dto.request;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import br.com.example.buyfood.model.entity.ProductEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class IngredientRequestDTO {

    @NotBlank
    private String ingredient;
    private String portion;
    private int status = 1;
}