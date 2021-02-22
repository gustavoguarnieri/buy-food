package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.DeliveryTaxPutRequestDTO;
import br.com.example.buyfood.model.dto.request.DeliveryTaxRequestDTO;
import br.com.example.buyfood.model.dto.response.DeliveryTaxResponseDTO;
import br.com.example.buyfood.model.entity.DeliveryTaxEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.DeliveryTaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliveryTaxService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DeliveryTaxRepository deliveryTaxRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private UserService userService;

    public List<DeliveryTaxResponseDTO> getDeliveryTaxList(Long establishmentId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return deliveryTaxRepository.findAllByEstablishment(establishment).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getDeliveryTaxListByEstablishmentAndStatus(establishment, RegisterStatus.ENABLED);
                case 0: {
                    return getDeliveryTaxListByEstablishmentAndStatus(establishment, RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public DeliveryTaxResponseDTO getDeliveryTax(Long establishmentId, Long deliveryTaxId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return deliveryTaxRepository.findByEstablishmentAndId(establishment, deliveryTaxId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    public DeliveryTaxResponseDTO createDeliveryTax(Long establishmentId, DeliveryTaxRequestDTO deliveryTaxRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        if (deliveryTaxRepository.findByEstablishment(establishment).isPresent()) {
            log.warn("createDeliveryTax: establishment already exist establishmentId={}", establishmentId);
            throw new BadRequestException("Establishment already exist");
        }

        var convertedDeliveryTaxEntity = convertToEntity(deliveryTaxRequestDto);
        convertedDeliveryTaxEntity.setEstablishment(establishment);
        return convertToDto(deliveryTaxRepository.save(convertedDeliveryTaxEntity));
    }

    public void updateDeliveryTax(Long establishmentId, Long deliveryTaxId,
                                  DeliveryTaxPutRequestDTO deliveryTaxPutRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var convertedDeliveryTaxEntity = convertToEntity(deliveryTaxPutRequestDto);
        convertedDeliveryTaxEntity.setId(deliveryTaxId);
        convertedDeliveryTaxEntity.setEstablishment(establishment);
        deliveryTaxRepository.save(convertedDeliveryTaxEntity);
    }

    public void deleteDeliveryTax(Long establishmentId, Long deliveryTaxId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var deliveryTaxEntity = getDeliveryTaxById(deliveryTaxId);
        deliveryTaxEntity.setStatus(RegisterStatus.DISABLED.getValue());
        deliveryTaxRepository.save(deliveryTaxEntity);
    }

    public DeliveryTaxEntity getDeliveryTaxById(Long deliveryTaxId) {
        return deliveryTaxRepository.findById(deliveryTaxId)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    private List<DeliveryTaxResponseDTO> getDeliveryTaxListByEstablishmentAndStatus(EstablishmentEntity establishment,
                                                                                    RegisterStatus enabled) {
        return deliveryTaxRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
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

    private DeliveryTaxResponseDTO convertToDto(DeliveryTaxEntity deliveryTaxEntity) {
        return modelMapper.map(deliveryTaxEntity, DeliveryTaxResponseDTO.class);
    }

    private DeliveryTaxEntity convertToEntity(Object deliveryTaxRequestDto) {
        return modelMapper.map(deliveryTaxRequestDto, DeliveryTaxEntity.class);
    }
}