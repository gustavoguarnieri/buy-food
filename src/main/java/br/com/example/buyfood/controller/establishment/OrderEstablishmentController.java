package br.com.example.buyfood.controller.establishment;

import br.com.example.buyfood.model.dto.request.OrderPutRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.service.establishment.OrderEstablishmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/orders")
public class OrderEstablishmentController {

  @Autowired private OrderEstablishmentService orderEstablishmentService;

  @GetMapping
  @ApiOperation(value = "Returns a list of orders")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of orders",
            response = OrderResponseDTO.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<OrderResponseDTO> getOrderList(
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Integer establishment) {
    log.info("getOrderList: starting to consult the list of orders");
    var orderResponseDtoList = orderEstablishmentService.getOrderList(status, establishment);
    log.info("getOrderList: finished to consult the list of orders");
    return orderResponseDtoList;
  }

  @GetMapping("/{orderId}")
  @ApiOperation(value = "Returns the informed order")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns the informed order",
            response = OrderResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public OrderResponseDTO getOrder(@Valid @NotBlank @PathVariable("orderId") Long orderId) {
    log.info("getOrder: starting to consult order by orderId={}", orderId);
    var orderResponseDtoList = orderEstablishmentService.getOrder(orderId);
    log.info("getOrder: finished to consult order by orderId={}", orderId);
    return orderResponseDtoList;
  }

  @PutMapping("/{orderId}")
  @ApiOperation(value = "Update order")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Updated order"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void updateOrder(
      @Valid @NotBlank @PathVariable("orderId") Long orderId,
      @Valid @RequestBody OrderPutRequestDTO orderPutRequestDto) {
    log.info("updateOrder: starting update order orderId={}", orderId);
    orderEstablishmentService.updateOrder(orderId, orderPutRequestDto);
    log.info("updateOrder: finished update order orderId={}", orderId);
  }
}
