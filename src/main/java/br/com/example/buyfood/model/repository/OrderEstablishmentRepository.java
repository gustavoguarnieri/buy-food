package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEstablishmentRepository extends JpaRepository<OrderEntity, Long> {
  List<OrderEntity> findAllByStatus(int status);

  List<OrderEntity> findAllByEstablishment(EstablishmentEntity establishment);

  List<OrderEntity> findAllByEstablishmentAndStatus(EstablishmentEntity establishment, int status);
}
