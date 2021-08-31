package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentDeliveryTaxPutRequestDTO;
import br.com.example.buyfood.model.dto.request.EstablishmentDeliveryTaxRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentDeliveryTaxResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentDeliveryTaxEntity;
import br.com.example.buyfood.model.repository.EstablishmentDeliveryTaxRepository;
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
public class EstablishmentDeliveryTaxService {

  private final ModelMapper modelMapper;

  private final EstablishmentDeliveryTaxRepository establishmentDeliveryTaxRepository;

  private final UserService userService;

  private final StatusValidation statusValidation;

  @Autowired
  public EstablishmentDeliveryTaxService(
      ModelMapper modelMapper,
      EstablishmentDeliveryTaxRepository establishmentDeliveryTaxRepository,
      UserService userService,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.establishmentDeliveryTaxRepository = establishmentDeliveryTaxRepository;
    this.userService = userService;
    this.statusValidation = statusValidation;
  }

  public List<EstablishmentDeliveryTaxResponseDTO> getDeliveryTaxList(Integer status) {
    if (status == null) {
      return establishmentDeliveryTaxRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return establishmentDeliveryTaxRepository
          .findAllByStatus(statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public EstablishmentDeliveryTaxResponseDTO getDeliveryTax(Long deliveryTaxId) {
    return establishmentDeliveryTaxRepository
        .findById(deliveryTaxId)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.DELIVERY_TAX_NOT_FOUND));
  }

  public List<EstablishmentDeliveryTaxResponseDTO> getMyDeliveryTaxList(Integer status) {
    if (status == null) {
      return establishmentDeliveryTaxRepository
          .findAllByAuditCreatedBy(userService.getUserId().orElse("-1"))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      switch (status) {
        case 1:
          return getMyDeliveryTaxListByAuditCreatedByAndStatus(RegisterStatus.ENABLED);
        case 0:
          {
            return getMyDeliveryTaxListByAuditCreatedByAndStatus(RegisterStatus.DISABLED);
          }
        default:
          throw new BadRequestException("Status incompatible");
      }
    }
  }

  public EstablishmentDeliveryTaxResponseDTO createDeliveryTax(
      EstablishmentDeliveryTaxRequestDTO establishmentDeliveryTaxRequestDto) {
    var convertedDeliveryTaxEntity = convertToEntity(establishmentDeliveryTaxRequestDto);
    return convertToDto(establishmentDeliveryTaxRepository.save(convertedDeliveryTaxEntity));
  }

  public void updateDeliveryTax(
      Long deliveryTaxId,
      EstablishmentDeliveryTaxPutRequestDTO establishmentDeliveryTaxPutRequestDto) {
    var convertedDeliveryTaxEntity = convertToEntity(establishmentDeliveryTaxPutRequestDto);
    convertedDeliveryTaxEntity.setId(deliveryTaxId);
    establishmentDeliveryTaxRepository.save(convertedDeliveryTaxEntity);
  }

  public void deleteDeliveryTax(Long deliveryTaxId) {
    var deliveryTaxEntity = getDeliveryTaxById(deliveryTaxId);
    deliveryTaxEntity.setStatus(RegisterStatus.DISABLED.getValue());
    establishmentDeliveryTaxRepository.save(deliveryTaxEntity);
  }

  public EstablishmentDeliveryTaxEntity getDeliveryTaxById(Long deliveryTaxId) {
    return establishmentDeliveryTaxRepository
        .findById(deliveryTaxId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.DELIVERY_TAX_NOT_FOUND));
  }

  private List<EstablishmentDeliveryTaxResponseDTO> getMyDeliveryTaxListByAuditCreatedByAndStatus(
      RegisterStatus enabled) {
    return establishmentDeliveryTaxRepository
        .findAllByAuditCreatedByAndStatus(userService.getUserId().orElse("-1"), enabled.getValue())
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private EstablishmentDeliveryTaxResponseDTO convertToDto(
      EstablishmentDeliveryTaxEntity establishmentDeliveryTaxEntity) {
    return modelMapper.map(
        establishmentDeliveryTaxEntity, EstablishmentDeliveryTaxResponseDTO.class);
  }

  private EstablishmentDeliveryTaxEntity convertToEntity(Object deliveryTaxRequestDto) {
    return modelMapper.map(deliveryTaxRequestDto, EstablishmentDeliveryTaxEntity.class);
  }
}
