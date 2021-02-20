package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.DeliveryAddressRequestDTO;
import br.com.example.buyfood.model.dto.response.DeliveryAddressResponseDTO;
import br.com.example.buyfood.model.entity.DeliveryAddressEntity;
import br.com.example.buyfood.model.entity.UserEntity;
import br.com.example.buyfood.model.repository.DeliveryAddressRepository;
import br.com.example.buyfood.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<DeliveryAddressResponseDTO> getUserAddressList(Integer status) {
        var userEntity = getUserByUserId(getUserId());

        if (status == null) {
            return getUserAddressByUserId(userEntity).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getUserAddressListByUserAndStatus(userEntity, RegisterStatus.ENABLED).stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toList());
                case 0: {
                    return getUserAddressListByUserAndStatus(userEntity, RegisterStatus.DISABLED).stream()
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
        var userEntity = getUserByUserId(getUserId());
        return convertToDto(getUserAddressByIdAndUser(addressId, userEntity));
    }

    public DeliveryAddressResponseDTO createUserAddress(DeliveryAddressRequestDTO deliveryAddressRequestDto) {
        var deliveryAddressEntity = convertToEntity(deliveryAddressRequestDto);
        deliveryAddressEntity.setUser(getUserByUserId(getUserId()));
        return convertToDto(deliveryAddressRepository.save(deliveryAddressEntity));
    }

    public void updateUserAddress(Long addressId, DeliveryAddressRequestDTO deliveryAddressRequestDto) {
        var userEntity = getUserByUserId(getUserId());
        getUserAddressByIdAndUser(addressId, userEntity);

        var deliveryAddressEntity = convertToEntity(deliveryAddressRequestDto);

        deliveryAddressEntity.setId(addressId);
        deliveryAddressEntity.setUser(userEntity);
        deliveryAddressRepository.save(deliveryAddressEntity);
    }

    public void deleteUserAddress(Long addressId) {
        var userEntity = getUserByUserId(getUserId());
        var userAddress = getUserAddressByIdAndUser(addressId, userEntity);
        userAddress.setStatus(RegisterStatus.DISABLED.getValue());
        deliveryAddressRepository.save(userAddress);
    }

    public UserEntity getUserByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private DeliveryAddressEntity getUserAddressByIdAndUser(Long addressId, UserEntity user) {
        return deliveryAddressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new NotFoundException("User address not found"));
    }

    private List<DeliveryAddressEntity> getUserAddressByUserId(UserEntity user) {
        return deliveryAddressRepository.findAllByUser(user);
    }

    private List<DeliveryAddressEntity> getUserAddressListByUserAndStatus(UserEntity user, RegisterStatus enabled) {
        return deliveryAddressRepository.findAllByUserAndStatus(user, enabled.getValue());
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private DeliveryAddressResponseDTO convertToDto(DeliveryAddressEntity deliveryAddressEntity) {
        return modelMapper.map(deliveryAddressEntity, DeliveryAddressResponseDTO.class);
    }

    private DeliveryAddressEntity convertToEntity(DeliveryAddressRequestDTO deliveryAddressRequestDto) {
        return modelMapper.map(deliveryAddressRequestDto, DeliveryAddressEntity.class);
    }
}