package br.com.example.buyfood.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileStorageFolder {
    ESTABLISHMENTS("/establishments/"),
    PRODUCTS("/products/");

    private final String value;
}