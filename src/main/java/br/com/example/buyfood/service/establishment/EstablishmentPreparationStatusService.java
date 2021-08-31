package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.PreparationStatusRequestDTO;
import br.com.example.buyfood.model.dto.response.PreparationStatusResponseDTO;
import br.com.example.buyfood.model.entity.PreparationStatusEntity;
import br.com.example.buyfood.model.repository.PreparationStatusRepository;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstablishmentPreparationStatusService {

  private final ModelMapper modelMapper;

  private final PreparationStatusRepository preparationStatusRepository;

  private final StatusValidation statusValidation;

  @Autowired
  public EstablishmentPreparationStatusService(
      ModelMapper modelMapper,
      PreparationStatusRepository preparationStatusRepository,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.preparationStatusRepository = preparationStatusRepository;
    this.statusValidation = statusValidation;
  }

  public List<PreparationStatusResponseDTO> getPreparationStatusList(Integer status) {
    if (status == null) {
      return preparationStatusRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return preparationStatusRepository
          .findAllByStatus(statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public PreparationStatusResponseDTO getPreparationStatus(Long id) {
    return preparationStatusRepository
        .findById(id)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PREPARATION_STATUS_NOT_FOUND));
  }

  public PreparationStatusResponseDTO createPreparationStatus(
      PreparationStatusRequestDTO preparationStatusRequestDTO) {
    var convertedPreparationStatusEntity = convertToEntity(preparationStatusRequestDTO);
    return convertToDto(preparationStatusRepository.save(convertedPreparationStatusEntity));
  }

  public void updatePreparationStatus(
      Long id, PreparationStatusRequestDTO preparationStatusRequestDTO) {
    var convertedPreparationStatusEntity = convertToEntity(preparationStatusRequestDTO);
    convertedPreparationStatusEntity.setId(id);
    preparationStatusRepository.save(convertedPreparationStatusEntity);
  }

  public void deletePreparationStatus(Long id) {
    var convertedPreparationStatusEntity = getPreparationStatusById(id);
    convertedPreparationStatusEntity.setStatus(RegisterStatus.DISABLED.getValue());
    preparationStatusRepository.save(convertedPreparationStatusEntity);
  }

  public PreparationStatusEntity getPreparationStatusById(Long id) {
    return preparationStatusRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PREPARATION_STATUS_NOT_FOUND));
  }

  private PreparationStatusResponseDTO convertToDto(
      PreparationStatusEntity preparationStatusEntity) {
    return modelMapper.map(preparationStatusEntity, PreparationStatusResponseDTO.class);
  }

  private PreparationStatusEntity convertToEntity(
      PreparationStatusRequestDTO preparationStatusRequestDTO) {
    return modelMapper.map(preparationStatusRequestDTO, PreparationStatusEntity.class);
  }
}
