package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "delivery_address")
public class DeliveryAddressEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String recipientName;

  @NotBlank
  @Column(nullable = false)
  private String zipCode;

  @NotBlank
  @Column(nullable = false)
  private String address;

  @NotNull
  @Column(nullable = false)
  private Integer addressNumber;

  @NotBlank
  @Column(nullable = false)
  private String neighbourhood;

  @NotBlank
  @Column(nullable = false)
  private String city;

  @NotBlank
  @Column(nullable = false)
  private String state;

  private String observation;

  @Column(nullable = false)
  private int status = RegisterStatus.ENABLED.getValue();

  @Embedded private Audit audit = new Audit();
}
