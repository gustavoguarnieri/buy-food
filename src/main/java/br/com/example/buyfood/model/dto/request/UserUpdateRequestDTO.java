package br.com.example.buyfood.model.dto.request;

import br.com.example.buyfood.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserUpdateRequestDTO {

    private String email;
    @NotBlank
    private String firstName;
    private String lastName;
    private String nickName;
    private String phone;
    private String password;
    private Role role;
}
