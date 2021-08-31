package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursPutRequestDTO;
import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentBusinessHoursResponseDTO;
import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.BusinessHoursRepository;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstablishmentBusinessHoursService {

  private final ModelMapper modelMapper;

  private final BusinessHoursRepository businessHoursRepository;

  private final EstablishmentService establishmentService;

  private final UserService userService;

  private final StatusValidation statusValidation;

  @Autowired
  public EstablishmentBusinessHoursService(
      ModelMapper modelMapper,
      BusinessHoursRepository businessHoursRepository,
      EstablishmentService establishmentService,
      UserService userService,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.businessHoursRepository = businessHoursRepository;
    this.establishmentService = establishmentService;
    this.userService = userService;
    this.statusValidation = statusValidation;
  }

  public List<EstablishmentBusinessHoursResponseDTO> getBusinessHoursList(
      Long establishmentId, Integer status) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);

    if (status == null) {
      return businessHoursRepository.findAllByEstablishment(establishment).stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return businessHoursRepository
          .findAllByEstablishmentAndStatus(
              establishment, statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public EstablishmentBusinessHoursResponseDTO getBusinessHours(
      Long establishmentId, Long businessHoursId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    return businessHoursRepository
        .findByEstablishmentAndId(establishment, businessHoursId)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException("Business hours not found"));
  }

  public List<EstablishmentBusinessHoursResponseDTO> getMyBusinessHoursList(Integer status) {
    if (status == null) {
      return businessHoursRepository
          .findAllByAuditCreatedBy(userService.getUserId().orElse("-1"))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return businessHoursRepository
          .findAllByAuditCreatedByAndStatus(
              userService.getUserId().orElse("-1"),
              statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public EstablishmentBusinessHoursResponseDTO createBusinessHours(
      Long establishmentId,
      EstablishmentBusinessHoursRequestDTO establishmentBusinessHoursRequestDto) {

    var establishment = establishmentService.getEstablishmentById(establishmentId);

    if (establishmentExists(establishment)) {
      log.warn(
          "createBusinessHours: establishment already exist establishmentId={}", establishmentId);
      throw new BadRequestException("Establishment already exist");
    }

    var convertedBusinessHoursEntity = convertToEntity(establishmentBusinessHoursRequestDto);
    convertedBusinessHoursEntity.setEstablishment(establishment);

    var businessHoursEntity = businessHoursRepository.save(convertedBusinessHoursEntity);

    return convertToDto(businessHoursEntity);
  }

  public void updateBusinessHours(
      Long establishmentId,
      Long businessHoursId,
      EstablishmentBusinessHoursPutRequestDTO establishmentBusinessHoursPutRequestDto) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    var convertedBusinessHoursEntity = convertToEntity(establishmentBusinessHoursPutRequestDto);
    convertedBusinessHoursEntity.setId(businessHoursId);
    convertedBusinessHoursEntity.setEstablishment(establishment);
    businessHoursRepository.save(convertedBusinessHoursEntity);
  }

  public void deleteBusinessHours(Long establishmentId, Long businessHoursId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    var businessHoursEntity = getBusinessHoursById(businessHoursId);
    businessHoursEntity.setStatus(RegisterStatus.DISABLED.getValue());
    businessHoursRepository.save(businessHoursEntity);
  }

  public BusinessHoursEntity getBusinessHoursById(Long businessHoursId) {
    return businessHoursRepository
        .findById(businessHoursId)
        .orElseThrow(() -> new NotFoundException("Business hours not found"));
  }

  private boolean establishmentExists(EstablishmentEntity establishment) {
    return businessHoursRepository.findByEstablishment(establishment).isPresent();
  }

  private String getUserId() {
    return userService
        .getUserId()
        .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
  }

  private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
    if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
      throw new ForbiddenException(ErrorMessages.USER_IS_NOT_OWNER_OF_ESTABLISHMENT);
    }
  }

  private EstablishmentBusinessHoursResponseDTO convertToDto(BusinessHoursEntity businessEntity) {
    return modelMapper.map(businessEntity, EstablishmentBusinessHoursResponseDTO.class);
  }

  private BusinessHoursEntity convertToEntity(Object businessHoursRequestDto) {
    return modelMapper.map(businessHoursRequestDto, BusinessHoursEntity.class);
  }
}
