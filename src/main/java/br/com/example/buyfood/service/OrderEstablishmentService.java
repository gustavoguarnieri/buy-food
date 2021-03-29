package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.response.OrderResponseDTO;
import br.com.example.buyfood.model.entity.OrderEntity;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import br.com.example.buyfood.model.repository.OrderEstablishmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderEstablishmentService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderEstablishmentRepository orderEstablishmentRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    public List<OrderResponseDTO> getOrderList(Integer status, Integer establishment) {
        if (status == null) {
            if (establishment == null) {
                return orderEstablishmentRepository.findAll().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
            } else {
                var establishmentEntity =
                        establishmentRepository.findById(Long.valueOf(establishment))
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
                        establishmentRepository.findById(Long.valueOf(establishment))
                                .orElseThrow(() -> new NotFoundException("Establishment order not found"));

                return orderEstablishmentRepository.findAllByEstablishmentAndStatus(
                        establishmentEntity,
                        registerStatus.getValue()).stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
            }
        }
    }

    public OrderResponseDTO getOrder(Long orderId) {
        var establishmentOrder = orderEstablishmentRepository
                .findById(orderId).orElseThrow(() -> new NotFoundException("User not found"));
        return convertToDto(establishmentOrder);
    }

    private OrderResponseDTO convertToDto(OrderEntity orderEntity) {
        return modelMapper.map(orderEntity, OrderResponseDTO.class);
    }
}