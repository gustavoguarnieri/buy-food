package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.FileStorageFolder;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.repository.EstablishmentImageRepository;
import br.com.example.buyfood.service.FileStorageService;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablishmentImageService {

    private final ModelMapper modelMapper;

    private final EstablishmentImageRepository establishmentImageRepository;

    private final FileStorageService fileStorageService;

    private final EstablishmentService establishmentService;

    private final UserService userService;

    private final StatusValidation statusValidation;

    private final String RETRY_DELAY_TIME = "200";
    private final int RETRY_MAX_ATTEMPTS = 6;

    @Autowired
    public EstablishmentImageService(ModelMapper modelMapper, EstablishmentImageRepository establishmentImageRepository, FileStorageService fileStorageService, EstablishmentService establishmentService, UserService userService, StatusValidation statusValidation) {
        this.modelMapper = modelMapper;
        this.establishmentImageRepository = establishmentImageRepository;
        this.fileStorageService = fileStorageService;
        this.establishmentService = establishmentService;
        this.userService = userService;
        this.statusValidation = statusValidation;
    }

    public List<ImageResponseDTO> getEstablishmentImageList(Long establishmentId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return establishmentImageRepository.findAllByEstablishmentId(establishment.getId()).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return establishmentImageRepository
                    .findAllByEstablishmentIdAndStatus(establishment.getId(),
                            statusValidation.getStatusIdentification(status))
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public ImageResponseDTO getEstablishmentImage(Long establishmentId, Long imageId) {
        return convertToDto(getEstablishmentImageByEstablishmentAndImage(establishmentId, imageId));
    }

    public ImageResponseDTO createEstablishmentImage(Long establishmentId, MultipartFile file) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return saveImage(file, establishment);
    }

    private ImageResponseDTO saveImage(MultipartFile file, EstablishmentEntity establishment) {
        var downloadPath = getDownloadEstablishmentPath(establishment);

        var uploadFileResponse =
                fileStorageService.saveFile(
                        file, FileStorageFolder.ESTABLISHMENTS, establishment.getId(), downloadPath);

        var imageEntity = fileStorageService.createImageEntity(establishment, uploadFileResponse);
        establishmentImageRepository.save(imageEntity);

        return fileStorageService.createImageResponseDTO(
                imageEntity.getId(),
                uploadFileResponse,
                RegisterStatus.ENABLED.getValue()
        );
    }

    public List<ImageResponseDTO> createEstablishmentImageList(Long establishmentId, MultipartFile[] files) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        var downloadPath = getDownloadEstablishmentPath(establishment);

        var uploadFileResponse =
                fileStorageService.saveFileList(
                        files, FileStorageFolder.ESTABLISHMENTS, establishmentId, downloadPath);

        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        uploadFileResponse.forEach(
                file -> {
                    var imageEntity = fileStorageService.createImageEntity(establishment, file);
                    establishmentImageRepository.save(imageEntity);
                    imageResponseDTOList.add(
                            fileStorageService.createImageResponseDTO(
                                    imageEntity.getId(),
                                    file,
                                    RegisterStatus.ENABLED.getValue()
                            )
                    );
                });

        return imageResponseDTOList;
    }

    public void updateEstablishmentImage(Long establishmentId, Long imageId, ImageRequestDTO imageRequestDto) {
        getEstablishmentImage(establishmentId, imageId);

        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var imageEntity = convertToEntity(imageRequestDto);
        imageEntity.setId(imageId);
        imageEntity.setEstablishment(establishment);
        establishmentImageRepository.save(imageEntity);
    }

    public void deleteEstablishmentImage(Long establishmentId, Long imageId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var imageEntity = getEstablishmentImageByEstablishmentAndImage(establishmentId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        establishmentImageRepository.save(imageEntity);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delayExpression = RETRY_DELAY_TIME)
    )
    public ResponseEntity<Resource> getDownloadEstablishmentImage(
            Long establishmentId, String fileName, HttpServletRequest request) {
        return fileStorageService.downloadFile(
                FileStorageFolder.ESTABLISHMENTS, establishmentId, fileName, request);
    }

    private ImageEntity getEstablishmentImageByEstablishmentAndImage(
            Long establishmentId, Long imageId) {
        return establishmentImageRepository
                .findByIdAndEstablishmentId(imageId, establishmentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ESTABLISHMENT_IMAGE_NOT_FOUND));
    }

    private String getDownloadEstablishmentPath(EstablishmentEntity establishment) {
        return "/api/v1/establishments/" + establishment.getId() + "/images/download-file/";
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException(ErrorMessages.USER_IS_NOT_OWNER_OF_ESTABLISHMENT);
        }
    }

    private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDTO.class);
    }

    private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}
