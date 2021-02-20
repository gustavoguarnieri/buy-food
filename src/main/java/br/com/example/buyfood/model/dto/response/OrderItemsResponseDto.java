package br.com.example.buyfood.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsResponseDto {

    private Long id;
    private int lineCode;
    private Long productId;
    private Integer quantity;
    private int status;
}