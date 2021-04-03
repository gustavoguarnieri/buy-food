package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.PaymentWayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentWayRepository extends JpaRepository<PaymentWayEntity, Long> {
    List<PaymentWayEntity> findAllByStatus(int status);
}