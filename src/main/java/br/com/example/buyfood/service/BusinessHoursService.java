package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.BusinessHoursPutRequestDto;
import br.com.example.buyfood.model.dto.request.BusinessHoursRequestDto;
import br.com.example.buyfood.model.dto.response.BusinessHoursResponseDto;
import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.BusinessHoursRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessHoursService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BusinessHoursRepository businessHoursRepository;

    @Autowired
    private EstablishmentService establishmentService;

    public List<BusinessHoursResponseDto> getBusinessHoursList(Long establishmentId, Integer status) {
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

    public BusinessHoursResponseDto getBusinessHours(Long establishmentId, Long businessHoursId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return businessHoursRepository.findByEstablishmentAndId(establishment, businessHoursId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    public BusinessHoursResponseDto createBusinessHours(Long establishmentId,
                                                        BusinessHoursRequestDto businessHoursRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        if (businessHoursRepository.findByEstablishment(establishment).isPresent()) {
            log.warn("createBusinessHours: establishment already exist establishmentId={}", establishmentId);
            throw new BadRequestException("Establishment already exist");
        }

        BusinessHoursEntity convertedBusinessHoursEntity = convertToEntity(businessHoursRequestDto);
        convertedBusinessHoursEntity.setEstablishment(establishment);
        return convertToDto(businessHoursRepository.save(convertedBusinessHoursEntity));
    }

    public void updateBusinessHours(Long establishmentId, Long businessHoursId,
                                    BusinessHoursPutRequestDto businessHoursPutRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        BusinessHoursEntity convertedBusinessHoursEntity = convertToEntity(businessHoursPutRequestDto);
        convertedBusinessHoursEntity.setId(businessHoursId);
        convertedBusinessHoursEntity.setEstablishment(establishment);
        businessHoursRepository.save(convertedBusinessHoursEntity);
    }

    public void deleteBusinessHours(Long establishmentId, Long id) {
        establishmentService.getEstablishmentById(establishmentId);
        BusinessHoursEntity businessHoursEntity = getBusinessHoursById(id);
        businessHoursEntity.setStatus(RegisterStatus.DISABLED.getValue());
        businessHoursRepository.save(businessHoursEntity);
    }

    public BusinessHoursEntity getBusinessHoursById(Long id) {
        return businessHoursRepository.findById(id).orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    private List<BusinessHoursResponseDto> getBusinessHoursListByEstablishmentAndStatus(
            EstablishmentEntity establishment, RegisterStatus enabled) {
        return businessHoursRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BusinessHoursResponseDto convertToDto(BusinessHoursEntity businessEntity) {
        return modelMapper.map(businessEntity, BusinessHoursResponseDto.class);
    }

    private BusinessHoursEntity convertToEntity(Object businessHoursRequestDto) {
        return modelMapper.map(businessHoursRequestDto, BusinessHoursEntity.class);
    }
}