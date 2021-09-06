package br.com.example.buyfood.model.embeddable;

import br.com.example.buyfood.service.UserService;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Audit implements Serializable {

  private static final long serialVersionUID = 1L;

  public Audit(String createdBy) {
    this.createdBy = createdBy;
  }

  @Column(updatable = false)
  private String createdBy;

  @Column(updatable = false)
  private LocalDateTime creationDate;

  private String lastUpdatedBy;

  private LocalDateTime lastUpdatedDate;

  @PrePersist
  public void prePersist() {
    creationDate = LocalDateTime.now();
    createdBy = new UserService().getUserId().orElse("-1");
  }

  @PreUpdate
  public void preUpdate() {
    lastUpdatedDate = LocalDateTime.now();
    lastUpdatedBy = new UserService().getUserId().orElse("-1");
  }
}
