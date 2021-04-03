package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.OrderEntity;

import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderUserRepository extends JpaRepository<OrderEntity, Long> {

    @Query("select function('date_format', o.audit.creationDate, '%m/%y') as indice, count(o) as value " +
            "from OrderEntity o " +
            "where o.audit.creationDate >= :startDate " +
            "group by function('date_format', o.audit.creationDate, '%m/%y')")
    List<DashboardStatisticsInterface> findOrdersByMonth(LocalDateTime startDate);

    @Query("select function('date_format', o.audit.creationDate, '%m/%y') as indice, sum(oi.price * oi.quantity) as value " +
            "from OrderEntity o, " +
            "OrderItemsEntity oi " +
            "where o.id = oi.order.id " +
            "and o.audit.creationDate >= :startDate " +
            "group by function('date_format', o.audit.creationDate, '%m/%y')")
    List<DashboardStatisticsInterface> findBillingByMonth(LocalDateTime startDate);

    @Query("select pse.description as indice, count(o.id) as value " +
            "from OrderEntity o, " +
            "PreparationStatusEntity pse " +
            "where o.preparationStatus = pse.id " +
            "and o.audit.creationDate >= :startDate " +
            "group by pse.description")
    List<DashboardStatisticsInterface> findPreparationStatus(LocalDateTime startDate);

    @Query("select o.paymentWay as indice, count(o.id) as value " +
            "from OrderEntity o " +
            "where o.audit.creationDate >= :startDate " +
            "group by o.paymentWay")
    List<DashboardStatisticsInterface> findPaymentWay(LocalDateTime startDate);

    @Query("select function('date_format', o.audit.creationDate, '%m/%y') as indice, count(o.id) as value " +
            "from OrderEntity o " +
            "where o.audit.creationDate >= :startDate " +
            "and o.paymentStatus = :paymentStatus " +
            "group by function('date_format', o.audit.creationDate, '%m/%y')")
    List<DashboardStatisticsInterface> findPaymentDeclinedStatus(LocalDateTime startDate, String paymentStatus);

    List<OrderEntity> findAllByStatus(int status);

    List<OrderEntity> findAllByAuditCreatedBy(String userId);

    List<OrderEntity> findAllByAuditCreatedByAndStatus(String userId, int status);
}