package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentCategoryRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentCategoryResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentCategoryEntity;
import br.com.example.buyfood.model.repository.EstablishmentCategoryRepository;
import br.com.example.buyfood.util.StatusValidation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentCategoryService {

    private ModelMapper modelMapper;

    private EstablishmentCategoryRepository establishmentCategoryRepository;

    private final StatusValidation statusValidation;

    @Autowired
    public EstablishmentCategoryService(ModelMapper modelMapper, EstablishmentCategoryRepository establishmentCategoryRepository, StatusValidation statusValidation) {
        this.modelMapper = modelMapper;
        this.establishmentCategoryRepository = establishmentCategoryRepository;
        this.statusValidation = statusValidation;
    }

    public List<EstablishmentCategoryResponseDTO> getEstablishmentCategoryList(Integer status) {
        if (status == null) {
            return establishmentCategoryRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return establishmentCategoryRepository.findAllByStatus(statusValidation.getStatusIdentification(status))
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public EstablishmentCategoryResponseDTO getEstablishmentCategory(Long id) {
        return establishmentCategoryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_NOT_FOUND));
    }

    public EstablishmentCategoryResponseDTO createEstablishmentCategory(
            EstablishmentCategoryRequestDTO establishmentCategoryRequestDto) {
        var convertedEstablishmentCategoryEntity = convertToEntity(establishmentCategoryRequestDto);
        return convertToDto(establishmentCategoryRepository.save(convertedEstablishmentCategoryEntity));
    }

    public void updateEstablishmentCategory(Long id, EstablishmentCategoryRequestDTO establishmentRequestDto) {
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
        return establishmentCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_CATEGORY_NOT_FOUND));
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
