package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "payment_way")
public class PaymentWayEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}