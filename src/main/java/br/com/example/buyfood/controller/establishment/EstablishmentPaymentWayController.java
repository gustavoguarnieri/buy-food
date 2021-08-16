package br.com.example.buyfood.controller.establishment;

import br.com.example.buyfood.model.dto.request.PaymentWayRequestDTO;
import br.com.example.buyfood.model.dto.response.PaymentWayResponseDTO;
import br.com.example.buyfood.service.establishment.EstablishmentPaymentWayService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/payment-way")
public class EstablishmentPaymentWayController {

  @Autowired private EstablishmentPaymentWayService paymentWayService;

  @GetMapping
  @ApiOperation(value = "Returns a list of payment way")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of payment way",
            response = PaymentWayResponseDTO.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<PaymentWayResponseDTO> getPaymentWayList(
      @RequestParam(required = false) Integer status) {
    log.info("getPaymentWayList: starting to consult the list of payment way");
    var paymentWayResponseDtoList = paymentWayService.getPaymentWayList(status);
    log.info("getPaymentWayList: finished to consult the list payment way");
    return paymentWayResponseDtoList;
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Returns the informed payment way")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns the informed payment way",
            response = PaymentWayResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public PaymentWayResponseDTO getPaymentWay(@Valid @NotBlank @PathVariable("id") Long id) {
    log.info("getPaymentWay: starting to consult payment way by id={}", id);
    var paymentWayResponseDtoList = paymentWayService.getPaymentWay(id);
    log.info("getPaymentWay: finished to consult payment way by id={}", id);
    return paymentWayResponseDtoList;
  }

  @Secured({"ROLE_ADMIN"})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create a new payment way")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 201,
            message = "Created payment way",
            response = PaymentWayResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public PaymentWayResponseDTO createPaymentWay(
      @Valid @RequestBody PaymentWayRequestDTO paymentWayRequestDTO) {
    log.info("createPaymentWay: starting to create new payment way");
    var paymentWayResponseDto = paymentWayService.createPaymentWay(paymentWayRequestDTO);
    log.info("createPaymentWay: finished to create new payment way");
    return paymentWayResponseDto;
  }

  @Secured({"ROLE_ADMIN"})
  @PutMapping("/{id}")
  @ApiOperation(value = "Update payment way")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Updated payment way"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void updatePaymentWay(
      @Valid @NotBlank @PathVariable("id") Long id,
      @Valid @RequestBody PaymentWayRequestDTO paymentWayRequestDTO) {
    log.info("updatePaymentWay: starting update preparation status id={}", id);
    paymentWayService.updatePaymentWay(id, paymentWayRequestDTO);
    log.info("updatePaymentWay: finished update preparation status id={}", id);
  }

  @Secured({"ROLE_ADMIN"})
  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete payment way")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Deleted payment way"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void deletePaymentWay(@Valid @NotBlank @PathVariable("id") Long id) {
    log.info("deletePaymentWay: starting delete payment way id={}", id);
    paymentWayService.deletePaymentWay(id);
    log.info("deletePaymentWay: finished delete payment way id={}", id);
  }
}
