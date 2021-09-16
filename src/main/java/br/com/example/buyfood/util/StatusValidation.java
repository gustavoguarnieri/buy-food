package br.com.example.buyfood.util;

import br.com.example.buyfood.enums.RegisterStatus;
import org.springframework.stereotype.Component;

@Component
public class StatusValidation {

  public int getStatusIdentification(Integer status) {
    return status == 0 ? RegisterStatus.DISABLED.getValue() : RegisterStatus.ENABLED.getValue();
  }
}
