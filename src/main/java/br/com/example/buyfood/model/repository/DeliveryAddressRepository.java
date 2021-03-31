package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.DeliveryAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, Long> {
    List<DeliveryAddressEntity> findAllByStatus(int status);

    List<DeliveryAddressEntity> findAllByAuditCreatedBy(String userId);

    List<DeliveryAddressEntity> findAllByAuditCreatedByAndStatus(String userId, int status);

    Optional<DeliveryAddressEntity> findById(Long addressId);
}