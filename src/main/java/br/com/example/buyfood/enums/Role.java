package br.com.example.buyfood.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Role {
    ESTABLISHMENT,
    USER,
    ADMIN;

    public static Stream<Role> stream() {
        return Arrays.stream(Role.values());
    }
}