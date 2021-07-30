package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentCategoryRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentCategoryResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentCategoryEntity;
import br.com.example.buyfood.model.repository.EstablishmentCategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstablishmentCategoryService {

  @Autowired private ModelMapper modelMapper;

  @Autowired private EstablishmentCategoryRepository establishmentCategoryRepository;

  public List<EstablishmentCategoryResponseDTO> getEstablishmentCategoryList(Integer status) {
    if (status == null) {
      return establishmentCategoryRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      switch (status) {
        case 1:
          return getEstablishmentCategoryListByStatus(RegisterStatus.ENABLED);
        case 0:
          {
            return getEstablishmentCategoryListByStatus(RegisterStatus.DISABLED);
          }
        default:
          throw new BadRequestException("Status incompatible");
      }
    }
  }

  public EstablishmentCategoryResponseDTO getEstablishmentCategory(Long id) {
    return establishmentCategoryRepository
        .findById(id)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException("Establishment not found"));
  }

  public EstablishmentCategoryResponseDTO createEstablishmentCategory(
      EstablishmentCategoryRequestDTO establishmentCategoryRequestDto) {
    var convertedEstablishmentCategoryEntity = convertToEntity(establishmentCategoryRequestDto);
    return convertToDto(establishmentCategoryRepository.save(convertedEstablishmentCategoryEntity));
  }

  public void updateEstablishmentCategory(
      Long id, EstablishmentCategoryRequestDTO establishmentRequestDto) {
    var convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
    convertedEstablishmentEntity.setId(id);
    establishmentCategoryRepository.save(convertedEstablishmentEntity);
  }

  public void deleteEstablishmentCategory(Long id) {
    var establishmentEntity = getEstablishmentCategoryById(id);
    establishmentEntity.setStatus(RegisterStatus.DISABLED.getValue());
    establishmentCategoryRepository.save(establishmentEntity);
  }

  public EstablishmentCategoryEntity getEstablishmentCategoryById(Long id) {
    return establishmentCategoryRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Establishment category not found"));
  }

  private List<EstablishmentCategoryResponseDTO> getEstablishmentCategoryListByStatus(
      RegisterStatus enabled) {
    return establishmentCategoryRepository.findAllByStatus(enabled.getValue()).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private EstablishmentCategoryResponseDTO convertToDto(
      EstablishmentCategoryEntity establishmentCategoryEntity) {
    return modelMapper.map(establishmentCategoryEntity, EstablishmentCategoryResponseDTO.class);
  }

  private EstablishmentCategoryEntity convertToEntity(
      EstablishmentCategoryRequestDTO establishmentCategoryRequestDto) {
    return modelMapper.map(establishmentCategoryRequestDto, EstablishmentCategoryEntity.class);
  }
}
