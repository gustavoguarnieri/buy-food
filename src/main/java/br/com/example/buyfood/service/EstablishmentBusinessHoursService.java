package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursPutRequestDTO;
import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentBusinessHoursResponseDTO;
import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.BusinessHoursRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentBusinessHoursService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BusinessHoursRepository businessHoursRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private UserService userService;

    public List<EstablishmentBusinessHoursResponseDTO> getBusinessHoursList(Long establishmentId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return businessHoursRepository.findAllByEstablishment(establishment).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getBusinessHoursListByEstablishmentAndStatus(establishment, RegisterStatus.ENABLED);
                case 0: {
                    return getBusinessHoursListByEstablishmentAndStatus(establishment, RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public EstablishmentBusinessHoursResponseDTO getBusinessHours(Long establishmentId, Long businessHoursId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return businessHoursRepository.findByEstablishmentAndId(establishment, businessHoursId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    public List<EstablishmentBusinessHoursResponseDTO> getMyBusinessHoursList(Integer status) {
        if (status == null) {
            return businessHoursRepository.findAllByAuditCreatedBy(new UserService().getUserId().orElse("-1")).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getMyBusinessHoursListByAuditCreatedByAndStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getMyBusinessHoursListByAuditCreatedByAndStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public EstablishmentBusinessHoursResponseDTO createBusinessHours(Long establishmentId,
                                                                     EstablishmentBusinessHoursRequestDTO establishmentBusinessHoursRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        if (businessHoursRepository.findByEstablishment(establishment).isPresent()) {
            log.warn("createBusinessHours: establishment already exist establishmentId={}", establishmentId);
            throw new BadRequestException("Establishment already exist");
        }

        var convertedBusinessHoursEntity = convertToEntity(establishmentBusinessHoursRequestDto);
        convertedBusinessHoursEntity.setEstablishment(establishment);
        return convertToDto(businessHoursRepository.save(convertedBusinessHoursEntity));
    }

    public void updateBusinessHours(Long establishmentId, Long businessHoursId,
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
        return businessHoursRepository.findById(businessHoursId)
                .orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    private List<EstablishmentBusinessHoursResponseDTO> getBusinessHoursListByEstablishmentAndStatus(
            EstablishmentEntity establishment, RegisterStatus enabled) {
        return businessHoursRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<EstablishmentBusinessHoursResponseDTO> getMyBusinessHoursListByAuditCreatedByAndStatus(RegisterStatus enabled) {
        return businessHoursRepository.findAllByAuditCreatedByAndStatus(new UserService().getUserId().orElse("-1"), enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException("User is not owner of establishment");
        }
    }

    private EstablishmentBusinessHoursResponseDTO convertToDto(BusinessHoursEntity businessEntity) {
        return modelMapper.map(businessEntity, EstablishmentBusinessHoursResponseDTO.class);
    }

    private BusinessHoursEntity convertToEntity(Object businessHoursRequestDto) {
        return modelMapper.map(businessHoursRequestDto, BusinessHoursEntity.class);
    }
}