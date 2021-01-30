package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.EstablishmentRequestDto;
import br.com.example.buyfood.model.dto.response.EstablishmentResponseDto;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.repository.EstablishmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentService {

    private final ModelMapper modelMapper;

    private EstablishmentRepository establishmentRepository;

    public EstablishmentService(ModelMapper modelMapper, EstablishmentRepository establishmentRepository) {
        this.modelMapper = modelMapper;
        this.establishmentRepository = establishmentRepository;
    }

    public List<EstablishmentResponseDto> getEstablishmentList() {
        return establishmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EstablishmentResponseDto getEstablishment(Long id) {
        return establishmentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Establishment not found"));
    }

    public EstablishmentResponseDto createEstablishment(EstablishmentRequestDto establishmentRequestDto) {
        EstablishmentEntity establishmentEntity = convertToEntity(establishmentRequestDto);
        return convertToDto(establishmentRepository.save(establishmentEntity));
    }

    public void updateEstablishment(Long id, EstablishmentRequestDto establishmentRequestDto) {
        getEstablishmentById(id);
        EstablishmentEntity establishmentEntity = convertToEntity(establishmentRequestDto);
        establishmentEntity.setId(id);
        establishmentRepository.save(establishmentEntity);
    }

    public void deleteEstablishment(Long id) {
        EstablishmentEntity establishmentEntity = getEstablishmentById(id);
        establishmentEntity.setStatus(RegisterStatus.DISABLED.getValue());
        establishmentRepository.save(establishmentEntity);
    }

    private EstablishmentEntity getEstablishmentById(Long id) {
        return establishmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Establishment not found"));
    }

    private EstablishmentResponseDto convertToDto (EstablishmentEntity establishmentEntity) {
        return modelMapper.map(establishmentEntity, EstablishmentResponseDto.class);
    }

    private EstablishmentEntity convertToEntity (EstablishmentRequestDto establishmentRequestDto) {
        return modelMapper.map(establishmentRequestDto, EstablishmentEntity.class);
    }
}