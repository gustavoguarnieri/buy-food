package br.com.example.buyfood.model.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "establishment_category")
public class EstablishmentCategoryEntity extends BaseDescriptionEntity implements Serializable {

  private static final long serialVersionUID = 1L;
}
