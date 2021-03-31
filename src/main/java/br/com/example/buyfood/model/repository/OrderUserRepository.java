package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderUserRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatus(int status);

    List<OrderEntity> findAllByAuditCreatedBy(String userId);

    List<OrderEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}