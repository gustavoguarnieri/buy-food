package br.com.example.buyfood.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequestDto {

    @NotBlank
    private String name;
    private String nickName;
    @NotBlank
    private String cpf;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotNull
    private LocalDate birthDate;
}
