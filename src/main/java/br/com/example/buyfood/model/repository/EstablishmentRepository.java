package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstablishmentRepository extends JpaRepository<EstablishmentEntity, Long> {
    List<EstablishmentEntity> findAllByStatus(int status);
    List<EstablishmentEntity> findAllByAuditCreatedBy(String userId);
    List<EstablishmentEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}