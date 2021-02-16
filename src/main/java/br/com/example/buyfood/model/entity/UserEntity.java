package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_keycloak")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserEntity(String userId, @NotBlank String firstName, @NotBlank String lastName, String nickName, @NotBlank String email, @NotBlank String phone, LocalDate birthDate, @CPF String cpf, @CNPJ String cnpj, Audit audit) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.audit = audit;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DeliveryAddressEntity> deliveryAddresses;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    private String nickName;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    private String phone;

    private LocalDate birthDate;

    @CPF
    private String cpf;

    @CNPJ
    private String cnpj;

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}