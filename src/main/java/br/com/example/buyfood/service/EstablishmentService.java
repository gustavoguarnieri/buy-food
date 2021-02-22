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

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private UserService userService;

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
        var convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
        return convertToDto(establishmentRepository.save(convertedEstablishmentEntity));
    }

    public void updateEstablishment(Long id, EstablishmentRequestDTO establishmentRequestDto) {
        getEstablishmentById(id);
        var convertedEstablishmentEntity = convertToEntity(establishmentRequestDto);
        validUserOwnerOfEstablishment(convertedEstablishmentEntity);
        convertedEstablishmentEntity.setId(id);
        establishmentRepository.save(convertedEstablishmentEntity);
    }

    public void deleteEstablishment(Long id) {
        var establishmentEntity = getEstablishmentById(id);
        validUserOwnerOfEstablishment(establishmentEntity);
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

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException("User is not owner of establishment");
        }
    }

    private EstablishmentResponseDTO convertToDto(EstablishmentEntity establishmentEntity) {
        return modelMapper.map(establishmentEntity, EstablishmentResponseDTO.class);
    }

    private EstablishmentEntity convertToEntity(EstablishmentRequestDTO establishmentRequestDto) {
        return modelMapper.map(establishmentRequestDto, EstablishmentEntity.class);
    }
}