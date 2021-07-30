package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.OrderItemsPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderPutRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.entity.OrderItemsEntity;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import br.com.example.buyfood.model.repository.OrderEstablishmentRepository;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEstablishmentService {

  @Autowired private ModelMapper modelMapper;

  @Autowired private ProductEstablishmentService productEstablishmentService;

  @Autowired private PreparationStatusService preparationStatusService;

  @Autowired private PaymentWayService paymentWayService;

  @Autowired private OrderItemsRepository orderItemsRepository;

  @Autowired private OrderEstablishmentRepository orderEstablishmentRepository;

  @Autowired private EstablishmentRepository establishmentRepository;

  public List<OrderResponseDTO> getOrderList(Integer status, Integer establishment) {
    if (status == null) {
      if (establishment == null) {
        return orderEstablishmentRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
      } else {
        var establishmentEntity =
            establishmentRepository
                .findById(Long.valueOf(establishment))
                .orElseThrow(() -> new NotFoundException("Establishment order not found"));

        return orderEstablishmentRepository.findAllByEstablishment(establishmentEntity).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
      }
    } else {
      var registerStatus = status == 0 ? RegisterStatus.DISABLED : RegisterStatus.ENABLED;

      if (establishment == null) {
        return orderEstablishmentRepository.findAllByStatus(registerStatus.getValue()).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
      } else {
        var establishmentEntity =
            establishmentRepository
                .findById(Long.valueOf(establishment))
                .orElseThrow(() -> new NotFoundException("Establishment order not found"));

        return orderEstablishmentRepository
            .findAllByEstablishmentAndStatus(establishmentEntity, registerStatus.getValue())
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
      }
    }
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

    orderEstablishmentRepository.save(orderEntity);

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

  private OrderEntity getOrderById(Long orderId) {
    return orderEstablishmentRepository
        .findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found"));
  }

  public OrderResponseDTO getOrder(Long orderId) {
    var establishmentOrder =
        orderEstablishmentRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("User not found"));
    return convertToDto(establishmentOrder);
  }

  private OrderResponseDTO convertToDto(OrderEntity orderEntity) {
    return modelMapper.map(orderEntity, OrderResponseDTO.class);
  }

  private OrderItemsEntity convertToEntity(OrderItemsPutRequestDTO orderItemsPutRequestDto) {
    return modelMapper.map(orderItemsPutRequestDto, OrderItemsEntity.class);
  }
}
