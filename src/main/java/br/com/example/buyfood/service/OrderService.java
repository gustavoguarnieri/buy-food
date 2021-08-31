package br.com.example.buyfood.service;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.OrderItemsPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.entity.OrderItemsEntity;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import br.com.example.buyfood.model.repository.OrderRepository;
import br.com.example.buyfood.service.establishment.EstablishmentPaymentWayService;
import br.com.example.buyfood.service.establishment.EstablishmentPreparationStatusService;
import br.com.example.buyfood.service.establishment.ProductEstablishmentService;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final ModelMapper modelMapper;

  private final ProductEstablishmentService productEstablishmentService;

  private final EstablishmentPreparationStatusService preparationStatusService;

  private final EstablishmentPaymentWayService paymentWayService;

  private final OrderItemsRepository orderItemsRepository;

  private final OrderRepository orderRepository;

  private final StatusValidation statusValidation;

  private final UserService userService;

  @Autowired
  public OrderService(
      ModelMapper modelMapper,
      ProductEstablishmentService productEstablishmentService,
      EstablishmentPreparationStatusService preparationStatusService,
      EstablishmentPaymentWayService paymentWayService,
      OrderItemsRepository orderItemsRepository,
      OrderRepository orderRepository,
      StatusValidation statusValidation,
      UserService userService) {
    this.modelMapper = modelMapper;
    this.productEstablishmentService = productEstablishmentService;
    this.preparationStatusService = preparationStatusService;
    this.paymentWayService = paymentWayService;
    this.orderItemsRepository = orderItemsRepository;
    this.orderRepository = orderRepository;
    this.statusValidation = statusValidation;
    this.userService = userService;
  }

  public void updateOrder(Long orderId, OrderPutRequestDTO orderPutRequestDto) {
    var orderEntity = getOrderById(orderId);

    orderEntity.setPaymentStatus(orderPutRequestDto.getPaymentStatus());

    var paymentWay = paymentWayService.getPaymentWayById(orderPutRequestDto.getPaymentWayId());
    orderEntity.setPaymentWay(paymentWay);

    var preparationStatus =
        preparationStatusService.getPreparationStatusById(
            orderPutRequestDto.getPreparationStatus().getId());

    orderEntity.setPreparationStatus(preparationStatus);

    orderEntity.setStatus(orderPutRequestDto.getStatus());

    orderRepository.save(orderEntity);

    orderPutRequestDto
        .getItems()
        .forEach(
            i -> {
              var convertedOrderItemEntity = convertToEntity(i);
              convertedOrderItemEntity.setOrder(orderEntity);
              convertedOrderItemEntity.setPrice(
                  productEstablishmentService
                      .getProduct(orderPutRequestDto.getEstablishmentId(), i.getProductId())
                      .getPrice());
              convertedOrderItemEntity.setStatus(i.getStatus());
              orderItemsRepository.save(convertedOrderItemEntity);
            });
  }

  public void deleteOrder(Long orderId) {
    var orderEntity = getOrderById(orderId);
    orderEntity.setStatus(RegisterStatus.DISABLED.getValue());
    orderRepository.save(orderEntity);
  }

  private OrderEntity getOrderById(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.ORDER_NOT_FOUND));
  }

  public OrderResponseDTO getOrder(Long orderId) {
    var establishmentOrder =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    return convertToDto(establishmentOrder);
  }

  public List<OrderResponseDTO> getOrderList(Integer status) {
    return orderRepository
        .findAllByStatus(statusValidation.getStatusIdentification(status))
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  public List<OrderResponseDTO> getOrderListByCreatedBy(Integer status) {
    return orderRepository
        .findAllByAuditCreatedByAndStatus(
            getUserId(), statusValidation.getStatusIdentification(status))
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private String getUserId() {
    return userService
        .getUserId()
        .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
  }

  protected OrderResponseDTO convertToDto(OrderEntity orderEntity) {
    return modelMapper.map(orderEntity, OrderResponseDTO.class);
  }

  protected OrderEntity convertToEntity(OrderRequestDTO orderRequestDto) {
    return modelMapper.map(orderRequestDto, OrderEntity.class);
  }

  protected OrderItemsEntity convertToEntity(OrderItemsPutRequestDTO orderItemsPutRequestDto) {
    return modelMapper.map(orderItemsPutRequestDto, OrderItemsEntity.class);
  }
}
