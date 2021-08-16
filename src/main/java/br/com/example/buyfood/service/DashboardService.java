package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.PaymentStatus;
import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import br.com.example.buyfood.model.repository.OrderUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DashboardService {

  private final int NINETY_DAYS = 90;

  private final OrderUserRepository orderUserRepository;

  @Autowired
  public DashboardService(OrderUserRepository orderUserRepository) {
    this.orderUserRepository = orderUserRepository;
  }

  public List<DashboardStatisticsInterface> getOrdersByMonthList() {
    return orderUserRepository.findOrdersByMonth(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getBillingByMonthList() {
    return orderUserRepository.findBillingByMonth(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPreparationStatusList() {
    return orderUserRepository.findPreparationStatus(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPaymentWayList() {
    return orderUserRepository.findPaymentWay(LocalDateTime.now().minusDays(NINETY_DAYS));
  }

  public List<DashboardStatisticsInterface> getPaymentDeclinedStatusList() {
    return orderUserRepository.findPaymentDeclinedStatus(
        LocalDateTime.now().minusDays(NINETY_DAYS), PaymentStatus.DECLINED.name());
  }
}
