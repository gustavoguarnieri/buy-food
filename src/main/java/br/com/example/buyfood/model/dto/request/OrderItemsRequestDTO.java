package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemsRequestDTO {

    @NotNull
    private Long id;
    private int lineCode;
    @NotNull
    private Integer quantity;
    @NotNull
    private Long productId;
}