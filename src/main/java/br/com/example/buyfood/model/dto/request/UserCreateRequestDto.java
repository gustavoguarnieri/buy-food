package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserCreateRequestDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    private int statusCode;
    private String status;
}
