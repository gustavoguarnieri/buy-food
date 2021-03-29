package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.OrderItemsPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderPutRequestDTO;
import br.com.example.buyfood.model.dto.request.OrderRequestDTO;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.entity.OrderItemsEntity;
import br.com.example.buyfood.model.entity.UserEntity;
import br.com.example.buyfood.model.repository.OrderItemsRepository;
import br.com.example.buyfood.model.repository.OrderUserRepository;
import br.com.example.buyfood.model.repository.UserRepository;
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
    private OrderUserRepository orderUserRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductEstablishmentService productEstablishmentService;

    @Autowired
    private AddressService addressService;

    public List<OrderResponseDTO> getOrderList(Integer status) {
        var userEntity = getUserByUserId(getUserId());

        if (status == null) {
            return getOrderByUser(userEntity).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getOrderListByUserAndStatus(userEntity, RegisterStatus.ENABLED).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                case 0: {
                    return getOrderListByUserAndStatus(userEntity, RegisterStatus.DISABLED).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public OrderResponseDTO getOrder(Long orderId) {
        var userEntity = getUserByUserId(getUserId());
        return convertToDto(getOrderByIdAndUser(orderId, userEntity));
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDto) {
        var userEntity = getUserByUserId(getUserId());
        var deliveryAddress =
                addressService.getUserAddressByIdAndUser(orderRequestDto.getDeliveryAddressId(), userEntity);

        var convertedOrderEntity = convertToEntity(orderRequestDto);
        convertedOrderEntity.setId(null);
        convertedOrderEntity.setUser(userEntity);
        convertedOrderEntity.setDeliveryAddress(deliveryAddress);

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
        var userEntity = getUserByUserId(getUserId());
        var orderEntity = getOrderByIdAndUser(orderId, userEntity);

        orderEntity.setPaymentWay(orderPutRequestDto.getPaymentWay());
        orderEntity.setPaymentStatus(orderPutRequestDto.getPaymentStatus());
        orderEntity.setStatus(orderPutRequestDto.getStatus());
        orderEntity.setPreparationStatus(orderPutRequestDto.getPreparationStatus());
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
        var userEntity = getUserByUserId(getUserId());
        var orderEntity = getOrderByIdAndUser(orderId, userEntity);
        orderEntity.setStatus(RegisterStatus.DISABLED.getValue());
        orderUserRepository.save(orderEntity);
    }

    private List<OrderEntity> getOrderListByUserAndStatus(UserEntity user, RegisterStatus enabled) {
        return orderUserRepository.findAllByUserAndStatus(user, enabled.getValue());
    }

    private List<OrderEntity> getOrderByUser(UserEntity user) {
        return orderUserRepository.findAllByUser(user);
    }

    private OrderEntity getOrderByIdAndUser(Long orderId, UserEntity user) {
        return orderUserRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new NotFoundException("User order not found"));
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserEntity getUserByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User not found"));
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