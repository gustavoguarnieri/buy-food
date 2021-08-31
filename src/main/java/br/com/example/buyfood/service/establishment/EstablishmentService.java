package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentCategoryEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.EstablishmentCategoryRepository;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentService {

    private final ModelMapper modelMapper;

    private final EstablishmentRepository establishmentRepository;

    private final EstablishmentCategoryRepository establishmentCategoryRepository;

    private final UserService userService;

    private final StatusValidation statusValidation;

    @Autowired
    public EstablishmentService(ModelMapper modelMapper, EstablishmentRepository establishmentRepository, EstablishmentCategoryRepository establishmentCategoryRepository, UserService userService, StatusValidation statusValidation) {
        this.modelMapper = modelMapper;
        this.establishmentRepository = establishmentRepository;
        this.establishmentCategoryRepository = establishmentCategoryRepository;
        this.userService = userService;
        this.statusValidation = statusValidation;
    }

    public List<EstablishmentResponseDTO> getEstablishmentList(Integer status) {
        if (status == null) {
            return establishmentRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return establishmentRepository.findAllByStatus(statusValidation.getStatusIdentification(status)).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public EstablishmentResponseDTO getEstablishment(Long id) {
        return establishmentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_NOT_FOUND));
    }

    public List<EstablishmentResponseDTO> getMyEstablishmentList(Integer status) {
        if (status == null) {
            return establishmentRepository.findAllByAuditCreatedBy(userService.getUserId().orElse("-1"))
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return establishmentRepository.findAllByAuditCreatedByAndStatus(
                            userService.getUserId().orElse("-1"), statusValidation.getStatusIdentification(status)
                    )
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public EstablishmentResponseDTO createEstablishment(EstablishmentRequestDTO establishmentRequestDto) {
        getEstablishmentCategoryById(establishmentRequestDto.getCategory().getId());
        var convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
        return convertToDto(establishmentRepository.save(convertedEstablishmentEntity));
    }

    public void updateEstablishment(Long id, EstablishmentRequestDTO establishmentRequestDto) {
        var establishmentEntity = getEstablishmentById(id);

        var convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);

        if (convertedEstablishmentEntity.getCategory() == null) {
            convertedEstablishmentEntity.setCategory(establishmentEntity.getCategory());
        }

        convertedEstablishmentEntity.setAudit(establishmentEntity.getAudit());
        validUserOwnerOfEstablishment(convertedEstablishmentEntity);
        convertedEstablishmentEntity.setId(id);

        establishmentRepository.save(convertedEstablishmentEntity);
    }

    public void deleteEstablishment(Long id) {
        var establishmentEntity = getEstablishmentById(id);
        establishmentEntity.setStatus(RegisterStatus.DISABLED.getValue());
        establishmentRepository.save(establishmentEntity);
    }

    public EstablishmentEntity getEstablishmentById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_NOT_FOUND));
    }

    public EstablishmentCategoryEntity getEstablishmentCategoryById(Long id) {
        return establishmentCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_CATEGORY_NOT_FOUND));
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException(ErrorMessages.USER_IS_NOT_OWNER_OF_ESTABLISHMENT);
        }
    }

    private EstablishmentResponseDTO convertToDto(EstablishmentEntity establishmentEntity) {
        return modelMapper.map(establishmentEntity, EstablishmentResponseDTO.class);
    }

    private EstablishmentEntity convertToEntity(EstablishmentRequestDTO establishmentRequestDto) {
        return modelMapper.map(establishmentRequestDto, EstablishmentEntity.class);
    }
}
