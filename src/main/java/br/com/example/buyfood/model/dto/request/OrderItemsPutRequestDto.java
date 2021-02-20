package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemsPutRequestDto {

    @NotNull
    private Long id;
    private int lineCode;
    @NotNull
    private Integer quantity;
    @NotNull
    private Long productId;
    private int status = 1;
}