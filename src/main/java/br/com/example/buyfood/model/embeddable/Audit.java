package br.com.example.buyfood.model.embeddable;

import br.com.example.buyfood.service.UserService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class Audit {

    @Column(updatable = false)
    private String createdBy;

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdatedDate;

    @PrePersist
    public void prePersist() {
        creationDate = LocalDateTime.now();
        createdBy = new UserService().getUserId();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedDate = LocalDateTime.now();
        lastUpdatedBy = new UserService().getUserId();
    }
}
