package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.interfaces.DashboardStatisticsInterface;
import br.com.example.buyfood.service.DashboardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

  @Autowired private DashboardService dashboardService;

  @GetMapping("/admin/orders-by-month")
  @ApiOperation(value = "Returns a list of orders by month")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of orders by month",
            response = DashboardStatisticsInterface.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<DashboardStatisticsInterface> getOrdersByMonthList() {
    log.info("getOrdersByMonthList: starting to consult the list of orders by month");
    var ordersByMonthResponseDto = dashboardService.getOrdersByMonthList();
    log.info("getOrdersByMonthList: finished to consult the list of orders by month");
    return ordersByMonthResponseDto;
  }

  @GetMapping("/admin/billing-by-month")
  @ApiOperation(value = "Returns a list of billing by month")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of billing by month",
            response = DashboardStatisticsInterface.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<DashboardStatisticsInterface> getBillingByMonthList() {
    log.info("getBillingByMonthList: starting to consult the list of orders by month");
    var billingByMonthList = dashboardService.getBillingByMonthList();
    log.info("getBillingByMonthList: finished to consult the list of orders by month");
    return billingByMonthList;
  }

  @GetMapping("/admin/preparation-status")
  @ApiOperation(value = "Returns a list of preparation status")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of preparation status",
            response = DashboardStatisticsInterface.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<DashboardStatisticsInterface> getPreparationStatusList() {
    log.info("getPreparationStatusList: starting to consult the preparation status list");
    var preparationStatusList = dashboardService.getPreparationStatusList();
    log.info("getPreparationStatusList: finished to consult the preparation status list");
    return preparationStatusList;
  }

  @GetMapping("/admin/payment-way")
  @ApiOperation(value = "Returns a list of payment way")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of payment way",
            response = DashboardStatisticsInterface.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<DashboardStatisticsInterface> getPaymentWayList() {
    log.info("getPaymentWayList: starting to consult the payment way list");
    var paymentWayList = dashboardService.getPaymentWayList();
    log.info("getPaymentWayList: finished to consult the payment way list");
    return paymentWayList;
  }

  @GetMapping("/admin/payment-declined-status")
  @ApiOperation(value = "Returns a list of payment declined status")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of declined status",
            response = DashboardStatisticsInterface.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<DashboardStatisticsInterface> getPaymentDeclinedStatusList() {
    log.info("getPaymentDeclinedStatusList: starting to consult the payment declined status list");
    var paymentDeclinedStatusList = dashboardService.getPaymentDeclinedStatusList();
    log.info("getPaymentDeclinedStatusList: finished to consult the payment declined status list");
    return paymentDeclinedStatusList;
  }
}
