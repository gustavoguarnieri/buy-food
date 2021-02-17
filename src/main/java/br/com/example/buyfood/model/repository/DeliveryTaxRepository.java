package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.DeliveryTaxEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTaxRepository extends JpaRepository<DeliveryTaxEntity, Long> {
    List<DeliveryTaxEntity> findAllByEstablishment(EstablishmentEntity establishment);

    List<DeliveryTaxEntity> findAllByEstablishmentAndStatus(EstablishmentEntity establishment, int status);

    Optional<DeliveryTaxEntity> findByEstablishmentAndId(EstablishmentEntity establishment, Long deliveryTaxId);

    Optional<DeliveryTaxEntity> findByEstablishment(EstablishmentEntity establishment);
}