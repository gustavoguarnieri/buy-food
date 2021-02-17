package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.DeliveryTaxPutRequestDto;
import br.com.example.buyfood.model.dto.request.DeliveryTaxRequestDto;
import br.com.example.buyfood.model.dto.response.DeliveryTaxResponseDto;
import br.com.example.buyfood.model.entity.DeliveryTaxEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.DeliveryTaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<DeliveryTaxResponseDto> getDeliveryTaxList(Long establishmentId, Integer status) {
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

    public DeliveryTaxResponseDto getDeliveryTax(Long establishmentId, Long deliveryTaxId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return deliveryTaxRepository.findByEstablishmentAndId(establishment, deliveryTaxId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    public DeliveryTaxResponseDto createDeliveryTax(Long establishmentId, DeliveryTaxRequestDto deliveryTaxRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        if (deliveryTaxRepository.findByEstablishment(establishment).isPresent()) {
            log.warn("createDeliveryTax: establishment already exist establishmentId={}", establishmentId);
            throw new BadRequestException("Establishment already exist");
        }

        DeliveryTaxEntity convertedDeliveryTaxEntity = convertToEntity(deliveryTaxRequestDto);
        convertedDeliveryTaxEntity.setEstablishment(establishment);
        return convertToDto(deliveryTaxRepository.save(convertedDeliveryTaxEntity));
    }

    public void updateDeliveryTax(Long establishmentId, Long deliveryTaxId,
                                  DeliveryTaxPutRequestDto deliveryTaxPutRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        DeliveryTaxEntity convertedDeliveryTaxEntity = convertToEntity(deliveryTaxPutRequestDto);
        convertedDeliveryTaxEntity.setId(deliveryTaxId);
        convertedDeliveryTaxEntity.setEstablishment(establishment);
        deliveryTaxRepository.save(convertedDeliveryTaxEntity);
    }

    public void deleteDeliveryTax(Long establishmentId, Long deliveryTaxId) {
        establishmentService.getEstablishmentById(establishmentId);
        DeliveryTaxEntity deliveryTaxEntity = getDeliveryTaxById(deliveryTaxId);
        deliveryTaxEntity.setStatus(RegisterStatus.DISABLED.getValue());
        deliveryTaxRepository.save(deliveryTaxEntity);
    }

    public DeliveryTaxEntity getDeliveryTaxById(Long deliveryTaxId) {
        return deliveryTaxRepository.findById(deliveryTaxId)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    private List<DeliveryTaxResponseDto> getDeliveryTaxListByEstablishmentAndStatus(EstablishmentEntity establishment,
                                                                                    RegisterStatus enabled) {
        return deliveryTaxRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DeliveryTaxResponseDto convertToDto(DeliveryTaxEntity deliveryTaxEntity) {
        return modelMapper.map(deliveryTaxEntity, DeliveryTaxResponseDto.class);
    }

    private DeliveryTaxEntity convertToEntity(Object deliveryTaxRequestDto) {
        return modelMapper.map(deliveryTaxRequestDto, DeliveryTaxEntity.class);
    }
}