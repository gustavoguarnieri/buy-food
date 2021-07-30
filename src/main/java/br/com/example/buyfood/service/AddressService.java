package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.DeliveryAddressRequestDTO;
import br.com.example.buyfood.model.dto.response.DeliveryAddressResponseDTO;
import br.com.example.buyfood.model.entity.DeliveryAddressEntity;
import br.com.example.buyfood.model.repository.DeliveryAddressRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddressService {

  @Autowired private ModelMapper modelMapper;

  @Autowired private DeliveryAddressRepository deliveryAddressRepository;

  @Autowired private UserService userService;

  public List<DeliveryAddressResponseDTO> getUserAddressList(Integer status) {
    if (status == null) {
      return deliveryAddressRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      switch (status) {
        case 1:
          return deliveryAddressRepository
              .findAllByStatus(RegisterStatus.ENABLED.getValue())
              .stream()
              .map(this::convertToDto)
              .collect(Collectors.toList());
        case 0:
          {
            return deliveryAddressRepository
                .findAllByStatus(RegisterStatus.DISABLED.getValue())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
          }
        default:
          log.error("getUserAddressList: Status incompatible, status:{}", status);
          throw new BadRequestException("Status incompatible");
      }
    }
  }

  public List<DeliveryAddressResponseDTO> getMyUserAddressList(Integer status) {
    if (status == null) {
      return deliveryAddressRepository.findAllByAuditCreatedBy(getUserId()).stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      switch (status) {
        case 1:
          return deliveryAddressRepository
              .findAllByAuditCreatedByAndStatus(getUserId(), RegisterStatus.ENABLED.getValue())
              .stream()
              .map(this::convertToDto)
              .collect(Collectors.toList());
        case 0:
          {
            return deliveryAddressRepository
                .findAllByAuditCreatedByAndStatus(getUserId(), RegisterStatus.DISABLED.getValue())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
          }
        default:
          log.error("getUserAddressList: Status incompatible, status:{}", status);
          throw new BadRequestException("Status incompatible");
      }
    }
  }

  public DeliveryAddressResponseDTO getUserAddress(Long addressId) {
    return convertToDto(getUserAddressById(addressId));
  }

  public DeliveryAddressResponseDTO createUserAddress(
      DeliveryAddressRequestDTO deliveryAddressRequestDto) {
    var deliveryAddressEntity = convertToEntity(deliveryAddressRequestDto);
    return convertToDto(deliveryAddressRepository.save(deliveryAddressEntity));
  }

  public void updateUserAddress(
      Long addressId, DeliveryAddressRequestDTO deliveryAddressRequestDto) {
    getUserAddressById(addressId);
    var deliveryAddressEntity = convertToEntity(deliveryAddressRequestDto);
    deliveryAddressEntity.setId(addressId);
    deliveryAddressRepository.save(deliveryAddressEntity);
  }

  public void deleteUserAddress(Long addressId) {
    var userAddress = getUserAddressById(addressId);
    userAddress.setStatus(RegisterStatus.DISABLED.getValue());
    deliveryAddressRepository.save(userAddress);
  }

  public DeliveryAddressEntity getUserAddressById(Long addressId) {
    return deliveryAddressRepository
        .findById(addressId)
        .orElseThrow(() -> new NotFoundException("Address not found"));
  }

  private String getUserId() {
    return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
  }

  private DeliveryAddressResponseDTO convertToDto(DeliveryAddressEntity deliveryAddressEntity) {
    return modelMapper.map(deliveryAddressEntity, DeliveryAddressResponseDTO.class);
  }

  private DeliveryAddressEntity convertToEntity(
      DeliveryAddressRequestDTO deliveryAddressRequestDto) {
    return modelMapper.map(deliveryAddressRequestDto, DeliveryAddressEntity.class);
  }
}
