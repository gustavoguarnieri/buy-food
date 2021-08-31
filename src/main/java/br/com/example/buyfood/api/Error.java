package br.com.example.buyfood.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

  private Integer statusCode;
  private String description;
  private OffsetDateTime dateTime;
  private List<Field> fields;

  public Error(
      Integer statusCode, String description, OffsetDateTime dateTime, List<Field> fields) {
    this.statusCode = statusCode;
    this.description = description;
    this.dateTime = dateTime;
    this.fields = fields;
  }

  @Getter
  @Setter
  public static class Field {
    private String name;
    private String message;

    public Field(String name, String message) {
      this.name = name;
      this.message = message;
    }
  }
}
