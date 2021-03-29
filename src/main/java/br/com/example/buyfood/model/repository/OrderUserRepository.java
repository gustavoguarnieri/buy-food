package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderUserRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByUser(UserEntity user);

    List<OrderEntity> findAllByUserAndStatus(UserEntity user, int status);

    Optional<OrderEntity> findByIdAndUser(Long orderId, UserEntity user);
}