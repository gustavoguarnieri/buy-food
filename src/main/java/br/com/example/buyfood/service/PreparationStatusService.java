package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.PreparationStatusRequestDTO;
import br.com.example.buyfood.model.dto.response.PreparationStatusResponseDTO;
import br.com.example.buyfood.model.entity.PreparationStatusEntity;
import br.com.example.buyfood.model.repository.PreparationStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PreparationStatusService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PreparationStatusRepository preparationStatusRepository;

    public List<PreparationStatusResponseDTO> getPreparationStatusList(Integer status) {
        if (status == null) {
            return preparationStatusRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getPreparationStatusListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getPreparationStatusListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public PreparationStatusResponseDTO getPreparationStatus(Long id) {
        return preparationStatusRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Preparation status not found"));
    }

    public PreparationStatusResponseDTO createPreparationStatus(PreparationStatusRequestDTO preparationStatusRequestDTO) {
        var convertedPreparationStatusEntity = convertToEntity(preparationStatusRequestDTO);
        return convertToDto(preparationStatusRepository.save(convertedPreparationStatusEntity));
    }

    public void updatePreparationStatus(Long id, PreparationStatusRequestDTO preparationStatusRequestDTO) {
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
        return preparationStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Preparation status not found"));
    }

    private List<PreparationStatusResponseDTO> getPreparationStatusListByStatus(RegisterStatus enabled) {
        return preparationStatusRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PreparationStatusResponseDTO convertToDto(PreparationStatusEntity preparationStatusEntity) {
        return modelMapper.map(preparationStatusEntity, PreparationStatusResponseDTO.class);
    }

    private PreparationStatusEntity convertToEntity(PreparationStatusRequestDTO preparationStatusRequestDTO) {
        return modelMapper.map(preparationStatusRequestDTO, PreparationStatusEntity.class);
    }
}