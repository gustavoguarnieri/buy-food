package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import br.com.example.buyfood.model.repository.OrderRepository;
import br.com.example.buyfood.service.OrderService;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEstablishmentService extends OrderService {

  private final OrderRepository orderRepository;

  private final EstablishmentRepository establishmentRepository;

  private final StatusValidation statusValidation;

  @Autowired
  public OrderEstablishmentService(ModelMapper modelMapper, ProductEstablishmentService productEstablishmentService, EstablishmentPreparationStatusService preparationStatusService, EstablishmentPaymentWayService paymentWayService, OrderItemsRepository orderItemsRepository, OrderRepository orderRepository, StatusValidation statusValidation, UserService userService, OrderRepository orderRepository1, EstablishmentRepository establishmentRepository, StatusValidation statusValidation1) {
    super(modelMapper, productEstablishmentService, preparationStatusService, paymentWayService, orderItemsRepository, orderRepository, statusValidation, userService);
    this.orderRepository = orderRepository1;
    this.establishmentRepository = establishmentRepository;
    this.statusValidation = statusValidation1;
  }

  public List<OrderResponseDTO> getOrderList(Integer status, Integer establishment) {
    return status == null
        ? getEstablishmentOrderList(establishment)
        : getEstablishmentOrderListByStatus(establishment, status);
  }

  private List<OrderResponseDTO> getEstablishmentOrderList(Integer establishmentId) {
    if (establishmentId == null) {
      return orderRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      var establishmentEntity = getEstablishmentById(establishmentId);

      return orderRepository.findAllByEstablishment(establishmentEntity).stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  private List<OrderResponseDTO> getEstablishmentOrderListByStatus(Integer establishmentId, Integer status) {
    var statusIdentification = statusValidation.getStatusIdentification(status);

    if (establishmentId == null) {
      return orderRepository
          .findAllByStatus(statusValidation.getStatusIdentification(statusIdentification))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      var establishmentEntity = getEstablishmentById(establishmentId);

      return orderRepository
          .findAllByEstablishmentAndStatus(establishmentEntity, statusIdentification)
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  private EstablishmentEntity getEstablishmentById(Integer establishmentId) {
    return establishmentRepository.findById(Long.valueOf(establishmentId))
        .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_ORDER_NOT_FOUND));
  }
}
