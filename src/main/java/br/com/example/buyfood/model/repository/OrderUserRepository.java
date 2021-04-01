package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.OrderEntity;

import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderUserRepository extends JpaRepository<OrderEntity, Long> {

    @Query("select function('date_format', o.audit.creationDate, '%m/%y') as indice, count(o) as qtt " +
        "from OrderEntity o " +
        "group by function('date_format', o.audit.creationDate, '%m/%y')")
    List<DashboardStatisticsInterface> findOrdersByMonth();

    List<OrderEntity> findAllByStatus(int status);

    List<OrderEntity> findAllByAuditCreatedBy(String userId);

    List<OrderEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}