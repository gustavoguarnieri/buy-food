package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserSigninRequestDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
