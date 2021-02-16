package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.DeliveryAddressEntity;
import br.com.example.buyfood.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, Long> {
    List<DeliveryAddressEntity> findAllByUser(UserEntity user);

    List<DeliveryAddressEntity> findAllByUserAndStatus(UserEntity user, int status);

    Optional<DeliveryAddressEntity> findByIdAndUser(Long addressId, UserEntity user);
}