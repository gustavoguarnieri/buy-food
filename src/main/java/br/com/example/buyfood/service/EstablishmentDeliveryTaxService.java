package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentDeliveryTaxPutRequestDTO;
import br.com.example.buyfood.model.dto.request.EstablishmentDeliveryTaxRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentDeliveryTaxResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentDeliveryTaxEntity;
import br.com.example.buyfood.model.repository.EstablishmentDeliveryTaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentDeliveryTaxService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EstablishmentDeliveryTaxRepository establishmentDeliveryTaxRepository;

    public List<EstablishmentDeliveryTaxResponseDTO> getDeliveryTaxList(Integer status) {
        if (status == null) {
            return establishmentDeliveryTaxRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getDeliveryTaxListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getDeliveryTaxListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public EstablishmentDeliveryTaxResponseDTO getDeliveryTax(Long deliveryTaxId) {
        return establishmentDeliveryTaxRepository.findById(deliveryTaxId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    public List<EstablishmentDeliveryTaxResponseDTO> getMyDeliveryTaxList(Integer status) {
        if (status == null) {
            return establishmentDeliveryTaxRepository.findAllByAuditCreatedBy(new UserService().getUserId().orElse("-1")).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getMyDeliveryTaxListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getMyDeliveryTaxListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public EstablishmentDeliveryTaxResponseDTO createDeliveryTax(EstablishmentDeliveryTaxRequestDTO establishmentDeliveryTaxRequestDto) {
        var convertedDeliveryTaxEntity = convertToEntity(establishmentDeliveryTaxRequestDto);
        return convertToDto(establishmentDeliveryTaxRepository.save(convertedDeliveryTaxEntity));
    }

    public void updateDeliveryTax(Long deliveryTaxId,
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
        return establishmentDeliveryTaxRepository.findById(deliveryTaxId)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    private List<EstablishmentDeliveryTaxResponseDTO> getDeliveryTaxListByStatus(RegisterStatus enabled) {
        return establishmentDeliveryTaxRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<EstablishmentDeliveryTaxResponseDTO> getMyDeliveryTaxListByStatus(RegisterStatus enabled) {
        return establishmentDeliveryTaxRepository.findAllByAuditCreatedByAndStatus(
                new UserService().getUserId().orElse("-1"), enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private EstablishmentDeliveryTaxResponseDTO convertToDto(EstablishmentDeliveryTaxEntity establishmentDeliveryTaxEntity) {
        return modelMapper.map(establishmentDeliveryTaxEntity, EstablishmentDeliveryTaxResponseDTO.class);
    }

    private EstablishmentDeliveryTaxEntity convertToEntity(Object deliveryTaxRequestDto) {
        return modelMapper.map(deliveryTaxRequestDto, EstablishmentDeliveryTaxEntity.class);
    }
}