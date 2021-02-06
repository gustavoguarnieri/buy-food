package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.DeliveryTaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTaxRepository extends JpaRepository<DeliveryTaxEntity, Long> {
    List<DeliveryTaxEntity> findAllByStatus(int status);
    Optional<DeliveryTaxEntity> findByIdAndStatus(Long id, int status);
    Optional<DeliveryTaxEntity> findByEstablishmentId(Long id);
}