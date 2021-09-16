package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.PaymentStatus;
import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import br.com.example.buyfood.model.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DashboardService {

  private static final int NINETY_DAYS = 90;

  private final OrderRepository orderRepository;

  @Autowired
  public DashboardService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public List<DashboardStatisticsInterface> getOrdersByMonthList() {
    return orderRepository.findOrdersByMonth(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getBillingByMonthList() {
    return orderRepository.findBillingByMonth(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPreparationStatusList() {
    return orderRepository.findPreparationStatus(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPaymentWayList() {
    return orderRepository.findPaymentWay(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPaymentDeclinedStatusList() {
    return orderRepository.findPaymentDeclinedStatus(
        LocalDateTime.now().minusDays(NINETY_DAYS), PaymentStatus.DECLINED.name());
  }
}
