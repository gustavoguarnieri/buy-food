package br.com.example.buyfood.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsResponseDTO {

    private Long id;
    private int lineCode;
    private ProductResponseDTO product;
    private Integer quantity;
    private int status;
}