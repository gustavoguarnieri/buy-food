package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "establishment")
public class EstablishmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "establishment", fetch = FetchType.LAZY)
    private List<ImageEntity> images;

    @OneToOne(mappedBy = "establishment", fetch = FetchType.LAZY)
    private BusinessHoursEntity businessHours;

    @OneToOne(mappedBy = "establishment", fetch = FetchType.LAZY)
    private DeliveryTaxEntity deliveryTax;

    @OneToMany(mappedBy = "establishment", fetch = FetchType.LAZY)
    private List<ProductEntity> product;

    @OneToMany(mappedBy = "establishment")
    private List<EstablishmentCategoryEntity> category;

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

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}