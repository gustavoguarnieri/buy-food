package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentDeliveryTaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstablishmentDeliveryTaxRepository extends JpaRepository<EstablishmentDeliveryTaxEntity, Long> {
    List<EstablishmentDeliveryTaxEntity> findAllByStatus(int status);
    Optional<EstablishmentDeliveryTaxEntity> findById(Long deliveryTaxId);
    List<EstablishmentDeliveryTaxEntity> findAllByAuditCreatedBy(String userId);
    List<EstablishmentDeliveryTaxEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}