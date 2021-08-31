package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.PreparationStatusDefault;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.OrderRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import br.com.example.buyfood.model.repository.OrderRepository;
import br.com.example.buyfood.model.repository.PreparationStatusRepository;
import br.com.example.buyfood.service.establishment.EstablishmentPaymentWayService;
import br.com.example.buyfood.service.establishment.EstablishmentPreparationStatusService;
import br.com.example.buyfood.service.establishment.ProductEstablishmentService;
import br.com.example.buyfood.util.StatusValidation;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderUserService extends OrderService {

  private final ProductEstablishmentService productEstablishmentService;

  private final EstablishmentPaymentWayService paymentWayService;

  private final EstablishmentPreparationStatusService preparationStatusService;

  private final AddressService addressService;

  private final OrderRepository orderRepository;

  private final PreparationStatusRepository preparationStatusRepository;

  private final OrderItemsRepository orderItemsRepository;

  private static final String PREPARATION_STATUS_PENDING_NOT_FOUND =
      "Não existe o status de preparo padrão: (Pendente)";

  @Autowired
  public OrderUserService(ModelMapper modelMapper, ProductEstablishmentService productEstablishmentService, EstablishmentPreparationStatusService preparationStatusService, EstablishmentPaymentWayService paymentWayService, OrderItemsRepository orderItemsRepository, OrderRepository orderRepository, StatusValidation statusValidation, UserService userService, ProductEstablishmentService productEstablishmentService1, EstablishmentPaymentWayService paymentWayService1, EstablishmentPreparationStatusService preparationStatusService1, AddressService addressService, OrderRepository orderRepository1, PreparationStatusRepository preparationStatusRepository, OrderItemsRepository orderItemsRepository1) {
    super(modelMapper, productEstablishmentService, preparationStatusService, paymentWayService, orderItemsRepository, orderRepository, statusValidation, userService);
    this.productEstablishmentService = productEstablishmentService1;
    this.paymentWayService = paymentWayService1;
    this.preparationStatusService = preparationStatusService1;
    this.addressService = addressService;
    this.orderRepository = orderRepository1;
    this.preparationStatusRepository = preparationStatusRepository;
    this.orderItemsRepository = orderItemsRepository1;
  }

  public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDto) {
    var convertedOrderEntity = convertToEntity(orderRequestDto);
    convertedOrderEntity.setId(null);

    var deliveryAddress = addressService.getUserAddressById(orderRequestDto.getDeliveryAddressId());
    convertedOrderEntity.setDeliveryAddress(deliveryAddress);

    var preparationStatusId =
        orderRequestDto.getPreparationStatus() == null
            ? preparationStatusRepository.findByDescriptionIgnoreCase(PreparationStatusDefault.PENDENTE.name())
                .orElseThrow(() -> new NotFoundException(PREPARATION_STATUS_PENDING_NOT_FOUND))
                .getId()
            : orderRequestDto.getPreparationStatus().getId();

    var preparationStatus = preparationStatusService.getPreparationStatusById(preparationStatusId);
    convertedOrderEntity.setPreparationStatus(preparationStatus);

    var paymentWay = paymentWayService.getPaymentWayById(orderRequestDto.getPaymentWayId());
    convertedOrderEntity.setPaymentWay(paymentWay);

    var count = new AtomicInteger(1);
    convertedOrderEntity
        .getItems()
        .forEach(
            i -> {
              i.setId(null);
              i.setLineCode(count.getAndIncrement());
              i.setPrice(
                  productEstablishmentService
                      .getProduct(orderRequestDto.getEstablishmentId(), i.getProduct().getId())
                      .getPrice());
              i.setOrder(convertedOrderEntity);
            });

    var orderEntity = orderRepository.save(convertedOrderEntity);
    orderItemsRepository.saveAll(convertedOrderEntity.getItems());
    return convertToDto(orderEntity);
  }
}
