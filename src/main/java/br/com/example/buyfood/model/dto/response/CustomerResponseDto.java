package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String nickName;
    private String cpf;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private Integer status;
}
