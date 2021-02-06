package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.DeliveryTaxRequestDto;
import br.com.example.buyfood.model.dto.response.DeliveryTaxResponseDto;
import br.com.example.buyfood.model.entity.DeliveryTaxEntity;
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

    public List<DeliveryTaxResponseDto> getDeliveryTaxList(Integer status) {
        if (status == null){
            return deliveryTaxRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getDeliveryTaxList(RegisterStatus.ENABLED);
                case 0: {
                    return getDeliveryTaxList(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public DeliveryTaxResponseDto getDeliveryTax(Long id) {
        return deliveryTaxRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    public DeliveryTaxResponseDto createDeliveryTax(DeliveryTaxRequestDto deliveryTaxRequestDto) {
        var establishment = establishmentService.getEstablishmentById(
                deliveryTaxRequestDto.getEstablishmentId());

        if (deliveryTaxRepository.findByEstablishmentId(deliveryTaxRequestDto.getEstablishmentId()).isPresent()) {
            log.warn("createDeliveryTax: establishment already exist establishmentId={}",
                    deliveryTaxRequestDto.getEstablishmentId());
            throw new BadRequestException("Establishment already exist");
        }

        DeliveryTaxEntity deliveryTaxEntity = convertToEntity(deliveryTaxRequestDto);
        deliveryTaxEntity.setEstablishment(establishment);
        return convertToDto(deliveryTaxRepository.save(deliveryTaxEntity));
    }

    public void updateDeliveryTax(Long id, DeliveryTaxRequestDto deliveryTaxRequestDto) {
        getDeliveryTaxById(id);
        establishmentService.getEstablishmentById(deliveryTaxRequestDto.getEstablishmentId());
        DeliveryTaxEntity deliveryTaxEntity = convertToEntity(deliveryTaxRequestDto);
        deliveryTaxEntity.setId(id);
        deliveryTaxEntity.setStatus(deliveryTaxRequestDto.getStatus());
        deliveryTaxRepository.save(deliveryTaxEntity);
    }

    public void deleteDeliveryTax(Long id) {
        DeliveryTaxEntity deliveryTaxEntity = getDeliveryTaxById(id);
        deliveryTaxEntity.setStatus(RegisterStatus.DISABLED.getValue());
        deliveryTaxRepository.save(deliveryTaxEntity);
    }

    public DeliveryTaxEntity getDeliveryTaxById(Long id) {
        return deliveryTaxRepository.findById(id).orElseThrow(() -> new NotFoundException("Delivery tax not found"));
    }

    private List<DeliveryTaxResponseDto> getDeliveryTaxList(RegisterStatus enabled) {
        return deliveryTaxRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DeliveryTaxResponseDto convertToDto (DeliveryTaxEntity deliveryTaxEntity) {
        return modelMapper.map(deliveryTaxEntity, DeliveryTaxResponseDto.class);
    }

    private DeliveryTaxEntity convertToEntity (DeliveryTaxRequestDto deliveryTaxRequestDto) {
        return modelMapper.map(deliveryTaxRequestDto, DeliveryTaxEntity.class);
    }
}