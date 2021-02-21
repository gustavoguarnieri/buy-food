package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.repository.EstablishmentImageRepository;
import br.com.example.buyfood.model.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentImageService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EstablishmentImageRepository establishmentImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EstablishmentService establishmentService;

    public List<ImageResponseDTO> getEstablishmentImageList(Long establishmentId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return getEstablishmentImageListById(establishment);
        } else {
            switch (status) {
                case 1:
                    return getEstablishmentImageListByIdAndStatus(establishment, RegisterStatus.ENABLED);
                case 0: {
                    return getEstablishmentImageListByIdAndStatus(establishment, RegisterStatus.DISABLED);
                }
                default:
                    log.error("getEstablishmentImageList: Status incompatible, status:{}", status);
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public ImageResponseDTO getEstablishmentImage(Long establishmentId, Long imageId) {
        return convertToDto(getEstablishmentImageByEstablishmentAndImage(establishmentId, imageId));
    }

    public ImageResponseDTO createEstablishmentImage(Long establishmentId, MultipartFile file) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return getSaveImageResponseDTO(file, establishment);
    }

    private ImageResponseDTO getSaveImageResponseDTO(MultipartFile file, EstablishmentEntity establishment) {
        var uploadFileResponse = fileStorageService.saveFile(file);

        var imageEntity = fileStorageService.createImageEntity(establishment, uploadFileResponse);
        establishmentImageRepository.save(imageEntity);

        return fileStorageService.createImageResponseDTO(imageEntity.getId(), uploadFileResponse, 1);
    }

    public List<ImageResponseDTO> createEstablishmentImageList(Long establishmentId, MultipartFile[] files) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        var uploadFileResponse = fileStorageService.saveFileList(files);

        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        uploadFileResponse
                .forEach(i -> {
                    var imageEntity = fileStorageService.createImageEntity(establishment, i);
                    establishmentImageRepository.save(imageEntity);
                    imageResponseDTOList.add(fileStorageService.createImageResponseDTO(imageEntity.getId(), i, 1));
                });

        return imageResponseDTOList;
    }

    public void updateEstablishmentImage(Long establishmentId, Long imageId, ImageRequestDTO imageRequestDto) {
        getEstablishmentImage(establishmentId, imageId);
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        ImageEntity imageEntity = convertToEntity(imageRequestDto);
        imageEntity.setId(imageId);
        imageEntity.setEstablishment(establishment);
        establishmentImageRepository.save(imageEntity);
    }

    public void deleteEstablishmentImage(Long establishmentId, Long imageId) {
        var imageEntity = getEstablishmentImageByEstablishmentAndImage(establishmentId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        establishmentImageRepository.save(imageEntity);
    }

    private ImageEntity getEstablishmentImageByEstablishmentAndImage(Long establishmentId, Long imageId) {
        return establishmentImageRepository.findByIdAndEstablishmentId(imageId, establishmentId)
                .orElseThrow(() -> new NotFoundException("Establishment image not found"));
    }

    private List<ImageResponseDTO> getEstablishmentImageListById(EstablishmentEntity establishment) {
        return establishmentImageRepository.findAllByEstablishmentId(establishment.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<ImageResponseDTO> getEstablishmentImageListByIdAndStatus(EstablishmentEntity establishment,
                                                                          RegisterStatus enabled) {
        return establishmentImageRepository.findAllByEstablishmentIdAndStatus(establishment.getId(), enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDTO.class);
    }

    private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}