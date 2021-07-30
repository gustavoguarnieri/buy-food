package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.PaymentWayEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentWayRepository extends JpaRepository<PaymentWayEntity, Long> {
  List<PaymentWayEntity> findAllByStatus(int status);
}
