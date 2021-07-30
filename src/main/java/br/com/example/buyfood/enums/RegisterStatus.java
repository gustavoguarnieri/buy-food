package br.com.example.buyfood.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegisterStatus {
  ENABLED(1),
  DISABLED(0);

  private final int value;
}
