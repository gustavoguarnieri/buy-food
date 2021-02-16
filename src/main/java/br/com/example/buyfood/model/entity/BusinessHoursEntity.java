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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "business_hours")
public class BusinessHoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "establishment_id", referencedColumnName = "id")
    private EstablishmentEntity establishment;

    private LocalTime startTimeFirstPeriodSunday;
    private LocalTime finalTimeFirstPeriodSunday;
    private LocalTime startTimeSecondPeriodSunday;
    private LocalTime finalTimeSecondPeriodSunday;
    private LocalTime startTimeFirstPeriodMonday;
    private LocalTime finalTimeFirstPeriodMonday;
    private LocalTime startTimeSecondPeriodMonday;
    private LocalTime finalTimeSecondPeriodMonday;
    private LocalTime startTimeFirstPeriodTuesday;
    private LocalTime finalTimeFirstPeriodTuesday;
    private LocalTime startTimeSecondPeriodTuesday;
    private LocalTime finalTimeSecondPeriodTuesday;
    private LocalTime startTimeFirstPeriodWednesday;
    private LocalTime finalTimeFirstPeriodWednesday;
    private LocalTime startTimeSecondPeriodWednesday;
    private LocalTime finalTimeSecondPeriodWednesday;
    private LocalTime startTimeFirstPeriodThursday;
    private LocalTime finalTimeFirstPeriodThursday;
    private LocalTime startTimeSecondPeriodThursday;
    private LocalTime finalTimeSecondPeriodThursday;
    private LocalTime startTimeFirstPeriodFriday;
    private LocalTime finalTimeFirstPeriodFriday;
    private LocalTime startTimeSecondPeriodFriday;
    private LocalTime finalTimeSecondPeriodFriday;
    private LocalTime startTimeFirstPeriodSaturday;
    private LocalTime finalTimeFirstPeriodSaturday;
    private LocalTime startTimeSecondPeriodSaturday;
    private LocalTime finalTimeSecondPeriodSaturday;

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}