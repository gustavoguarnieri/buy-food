package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.EstablishmentCategory;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.converter.EstablishmentCategoryConverter;
import br.com.example.buyfood.model.embeddable.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Table(name="establishment")
public class EstablishmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "establishment", fetch = FetchType.LAZY)
    private BusinessHoursEntity businessHours;

    @OneToOne(mappedBy = "establishment", fetch = FetchType.LAZY)
    private DeliveryTaxEntity deliveryTax;

    @NotBlank
    @Column(nullable = false)
    private String companyName;

    @NotBlank
    @Column(nullable = false)
    private String tradingName;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    private String commercialPhone;

    @NotBlank
    private String mobilePhone;

    @Convert(converter = EstablishmentCategoryConverter.class)
    private EstablishmentCategory category;

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}