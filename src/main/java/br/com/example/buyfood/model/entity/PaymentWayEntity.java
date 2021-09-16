package br.com.example.buyfood.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "payment_way")
public class PaymentWayEntity extends BaseDescriptionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}
