package br.com.example.buyfood.model.entity;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.embeddable.Audit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
public class ImageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public ImageEntity(ProductEntity product, @NotBlank String fileName, @NotNull String fileUri, String fileType, long size) {
        this.product = product;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileType = fileType;
        this.size = size;
    }

    public ImageEntity(EstablishmentEntity establishment, @NotBlank String fileName, @NotNull String fileUri, String fileType, long size) {
        this.establishment = establishment;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileType = fileType;
        this.size = size;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "establishment_id")
    private EstablishmentEntity establishment;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String fileName;

    @NotNull
    @Column(nullable = false)
    private String fileUri;

    private String fileType;

    private long size = 0;

    @Column(nullable = false)
    private int status = RegisterStatus.ENABLED.getValue();

    @Embedded
    private Audit audit = new Audit();
}