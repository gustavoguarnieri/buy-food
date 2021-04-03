package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.PreparationStatusDefault;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.OrderItemsPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.entity.OrderItemsEntity;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import br.com.example.buyfood.model.repository.OrderUserRepository;
import br.com.example.buyfood.model.repository.PreparationStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderUserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductEstablishmentService productEstablishmentService;

    @Autowired
    private PreparationStatusService preparationStatusService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderUserRepository orderUserRepository;

    @Autowired
    private PreparationStatusRepository preparationStatusRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    public List<OrderResponseDTO> getOrderList(Integer status) {
        if (status == null) {
            return orderUserRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return orderUserRepository
                            .findAllByStatus(RegisterStatus.ENABLED.getValue()).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                case 0: {
                    return orderUserRepository
                            .findAllByStatus(RegisterStatus.DISABLED.getValue()).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public List<OrderResponseDTO> getMyOrderList(Integer status) {
        if (status == null) {
            return orderUserRepository.findAllByAuditCreatedBy(getUserId()).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return orderUserRepository
                            .findAllByAuditCreatedByAndStatus(getUserId(), RegisterStatus.ENABLED.getValue()).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                case 0: {
                    return orderUserRepository
                            .findAllByAuditCreatedByAndStatus(getUserId(), RegisterStatus.DISABLED.getValue()).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public OrderResponseDTO getOrder(Long orderId) {
        return convertToDto(getOrderById(orderId));
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDto) {
        var convertedOrderEntity = convertToEntity(orderRequestDto);
        convertedOrderEntity.setId(null);

        var deliveryAddress =
                addressService.getUserAddressById(orderRequestDto.getDeliveryAddressId());
        convertedOrderEntity.setDeliveryAddress(deliveryAddress);

        var preparationStatusId = orderRequestDto.getPreparationStatus() == null ?
                preparationStatusRepository.findByDescriptionIgnoreCase(PreparationStatusDefault.PENDENTE.name())
                        .orElseThrow(() -> new NotFoundException("Não existe o status de preparo padrão: (Pendente)")).getId() :
                orderRequestDto.getPreparationStatus().getId();

        var preparationStatus =
                preparationStatusService.getPreparationStatusById(preparationStatusId);
        convertedOrderEntity.setPreparationStatus(preparationStatus);

        var count = new AtomicInteger(1);
        convertedOrderEntity.getItems().forEach(i -> {
            i.setId(null);
            i.setLineCode(count.getAndIncrement());
            i.setPrice(productEstablishmentService.getProduct(orderRequestDto.getEstablishmentId(), i.getProduct().getId()).getPrice());
            i.setOrder(convertedOrderEntity);
        });

        var orderEntity = orderUserRepository.save(convertedOrderEntity);
        orderItemsRepository.saveAll(convertedOrderEntity.getItems());
        return convertToDto(orderEntity);
    }

    public void updateOrder(Long orderId, OrderPutRequestDTO orderPutRequestDto) {
        var orderEntity = getOrderById(orderId);

        orderEntity.setPaymentWay(orderPutRequestDto.getPaymentWay());
        orderEntity.setPaymentStatus(orderPutRequestDto.getPaymentStatus());
        orderEntity.setStatus(orderPutRequestDto.getStatus());

        var preparationStatus =
                preparationStatusService.getPreparationStatusById(orderPutRequestDto.getPreparationStatus().getId());
        orderEntity.setPreparationStatus(preparationStatus);

        orderUserRepository.save(orderEntity);

        orderPutRequestDto.getItems().forEach(i -> {
            var convertedOrderItemEntity = convertToEntity(i);
            convertedOrderItemEntity.setOrder(orderEntity);
            convertedOrderItemEntity.setPrice(
                    productEstablishmentService.getProduct(orderPutRequestDto.getEstablishmentId(),
                            i.getProductId()).getPrice()
            );
            convertedOrderItemEntity.setStatus(i.getStatus());
            orderItemsRepository.save(convertedOrderItemEntity);
        });
    }

    public void deleteOrder(Long orderId) {
        var orderEntity = getOrderById(orderId);
        orderEntity.setStatus(RegisterStatus.DISABLED.getValue());
        orderUserRepository.save(orderEntity);
    }

    private OrderEntity getOrderById(Long orderId) {
        return orderUserRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private OrderResponseDTO convertToDto(OrderEntity orderEntity) {
        return modelMapper.map(orderEntity, OrderResponseDTO.class);
    }

    private OrderEntity convertToEntity(OrderRequestDTO orderRequestDto) {
        return modelMapper.map(orderRequestDto, OrderEntity.class);
    }

    private OrderItemsEntity convertToEntity(OrderItemsPutRequestDTO orderItemsPutRequestDto) {
        return modelMapper.map(orderItemsPutRequestDto, OrderItemsEntity.class);
    }
}