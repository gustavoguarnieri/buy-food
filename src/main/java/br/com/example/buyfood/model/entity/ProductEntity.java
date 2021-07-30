package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product")
public class ProductEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  private List<ImageEntity> images;

  @ManyToOne
  @JoinColumn(name = "establishment_id", referencedColumnName = "id")
  private EstablishmentEntity establishment;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  private List<IngredientEntity> ingredients;

  @NotBlank
  @Column(nullable = false, length = 50)
  private String name;

  @NotNull
  @Column(nullable = false)
  private BigDecimal price;

  @NotNull
  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private int status = RegisterStatus.ENABLED.getValue();

  @Embedded private Audit audit = new Audit();
}
