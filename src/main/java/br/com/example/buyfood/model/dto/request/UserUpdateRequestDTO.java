package br.com.example.buyfood.model.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDTO {

  private String email;
  @NotBlank private String firstName;
  private String lastName;
  private String nickName;
  private String phone;
  private String password;
  private String role;
}
