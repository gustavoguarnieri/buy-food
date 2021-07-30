package br.com.example.buyfood.enums;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
