package br.com.example.buyfood.model.embeddable;

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
    private Long createdBy;

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private Long lastUpdatedBy;

    private LocalDateTime lastUpdatedDate;

    @PrePersist
    public void prePersist() {
        creationDate = LocalDateTime.now();
        createdBy = -1L;
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedDate = LocalDateTime.now();
        lastUpdatedBy = -1L;
    }
}
