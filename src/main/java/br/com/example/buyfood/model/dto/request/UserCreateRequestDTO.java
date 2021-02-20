package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class UserCreateRequestDTO {

    @CPF
    private String cpf;
    @CNPJ
    private String cnpj;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String nickName;
    @NotBlank
    private String phone;
    private LocalDate birthDate;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
