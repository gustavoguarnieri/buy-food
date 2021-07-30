package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstablishmentBusinessHoursResponseDTO {

  private Long id;
  private EstablishmentResponseForBusinessHoursDTO establishment;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodSunday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodSunday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodSunday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodSunday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodMonday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodMonday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodMonday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodMonday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodTuesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodTuesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodTuesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodTuesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodWednesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodWednesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodWednesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodWednesday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodThursday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodThursday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodThursday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodThursday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodFriday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodFriday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodFriday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodFriday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeFirstPeriodSaturday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeFirstPeriodSaturday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTimeSecondPeriodSaturday;

  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime finalTimeSecondPeriodSaturday;

  private int status;
}
