package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.ImageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentImageRepository extends JpaRepository<ImageEntity, Long> {
  List<ImageEntity> findAllByEstablishmentIdAndStatus(Long establishmentId, int status);

  List<ImageEntity> findAllByEstablishmentId(Long establishmentId);

  Optional<ImageEntity> findByIdAndEstablishmentId(Long imageId, Long establishmentId);
}
