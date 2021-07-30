package br.com.example.buyfood.model.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSigninRequestDTO {

  @NotBlank @Email private String email;
  @NotBlank private String password;
}
