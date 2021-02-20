package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    public List<EstablishmentResponseDTO> getEstablishmentList(Integer status) {
        if (status == null) {
            return establishmentRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getEstablishmentListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getEstablishmentListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public EstablishmentResponseDTO getEstablishment(Long id) {
        return establishmentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Establishment not found"));
    }

    public EstablishmentResponseDTO createEstablishment(EstablishmentRequestDTO establishmentRequestDto) {
        EstablishmentEntity convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
        return convertToDto(establishmentRepository.save(convertedEstablishmentEntity));
    }

    public void updateEstablishment(Long id, EstablishmentRequestDTO establishmentRequestDto) {
        getEstablishmentById(id);
        EstablishmentEntity convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
        convertedEstablishmentEntity.setId(id);
        establishmentRepository.save(convertedEstablishmentEntity);
    }

    public void deleteEstablishment(Long id) {
        EstablishmentEntity establishmentEntity = getEstablishmentById(id);
        establishmentEntity.setStatus(RegisterStatus.DISABLED.getValue());
        establishmentRepository.save(establishmentEntity);
    }

    public EstablishmentEntity getEstablishmentById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Establishment not found"));
    }

    private List<EstablishmentResponseDTO> getEstablishmentListByStatus(RegisterStatus enabled) {
        return establishmentRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private EstablishmentResponseDTO convertToDto(EstablishmentEntity establishmentEntity) {
        return modelMapper.map(establishmentEntity, EstablishmentResponseDTO.class);
    }

    private EstablishmentEntity convertToEntity(EstablishmentRequestDTO establishmentRequestDto) {
        return modelMapper.map(establishmentRequestDto, EstablishmentEntity.class);
    }
}