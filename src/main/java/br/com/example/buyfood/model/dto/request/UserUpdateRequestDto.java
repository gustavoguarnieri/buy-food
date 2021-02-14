package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserUpdateRequestDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String nickName;
    @NotBlank
    private String phone;
    @NotBlank
    private String password;
}
