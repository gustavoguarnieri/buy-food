package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.PaymentStatus;
import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import br.com.example.buyfood.model.repository.OrderUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DashboardService {

    @Autowired
    private OrderUserRepository orderUserRepository;

    public List<DashboardStatisticsInterface> getOrdersByMonthList() {
        return orderUserRepository.findOrdersByMonth(LocalDateTime.now().minusDays(90));
    }

    public List<DashboardStatisticsInterface> getBillingByMonthList() {
        return orderUserRepository.findBillingByMonth(LocalDateTime.now().minusDays(90));
    }

    public List<DashboardStatisticsInterface> getPreparationStatusList() {
        return orderUserRepository.findPreparationStatus(LocalDateTime.now().minusDays(90));
    }

    public List<DashboardStatisticsInterface> getPaymentWayList() {
        return orderUserRepository.findPaymentWay(LocalDateTime.now().minusDays(90));
    }

    public List<DashboardStatisticsInterface> getPaymentDeclinedStatusList() {
        return orderUserRepository.findPaymentDeclinedStatus(LocalDateTime.now().minusDays(90), PaymentStatus.DECLINED.name());
    }
}