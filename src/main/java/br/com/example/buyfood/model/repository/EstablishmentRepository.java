package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentRepository extends JpaRepository<EstablishmentEntity, Long> {
  List<EstablishmentEntity> findAllByStatus(int status);

  List<EstablishmentEntity> findAllByAuditCreatedBy(String userId);

  List<EstablishmentEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}
