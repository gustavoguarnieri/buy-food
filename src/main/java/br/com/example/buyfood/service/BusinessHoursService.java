package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.BusinessHoursRequestDto;
import br.com.example.buyfood.model.dto.response.BusinessHoursResponseDto;
import br.com.example.buyfood.model.entity.BusinessHoursEntity;
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

    public List<BusinessHoursResponseDto> getBusinessHoursList(Integer status) {
        if (status == null){
            return businessHoursRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getBusinessHoursListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getBusinessHoursListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public BusinessHoursResponseDto getBusinessHours(Long id) {
        return businessHoursRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    public BusinessHoursResponseDto createBusinessHours(BusinessHoursRequestDto businessHoursRequestDto) {
        var establishment = establishmentService.getEstablishmentById(
                businessHoursRequestDto.getEstablishmentId());
        BusinessHoursEntity businessHoursEntity = convertToEntity(businessHoursRequestDto);
        businessHoursEntity.setEstablishment(establishment);
        return convertToDto(businessHoursRepository.save(businessHoursEntity));
    }

    public void updateBusinessHours(Long id, BusinessHoursRequestDto businessHoursRequestDto) {
        getBusinessHoursById(id);
        establishmentService.getEstablishmentById(businessHoursRequestDto.getEstablishmentId());
        BusinessHoursEntity businessHoursEntity = convertToEntity(businessHoursRequestDto);
        businessHoursEntity.setId(id);
        businessHoursRepository.save(businessHoursEntity);
    }

    public void deleteBusinessHours(Long id) {
        BusinessHoursEntity businessHoursEntity = getBusinessHoursById(id);
        businessHoursEntity.setStatus(RegisterStatus.DISABLED.getValue());
        businessHoursRepository.save(businessHoursEntity);
    }

    public BusinessHoursEntity getBusinessHoursById(Long id) {
        return businessHoursRepository.findById(id).orElseThrow(() -> new NotFoundException("Business hours not found"));
    }

    private List<BusinessHoursResponseDto> getBusinessHoursListByStatus(RegisterStatus enabled) {
        return businessHoursRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BusinessHoursResponseDto convertToDto (BusinessHoursEntity businessEntity) {
        return modelMapper.map(businessEntity, BusinessHoursResponseDto.class);
    }

    private BusinessHoursEntity convertToEntity (BusinessHoursRequestDto businessHoursRequestDto) {
        return modelMapper.map(businessHoursRequestDto, BusinessHoursEntity.class);
    }
}
