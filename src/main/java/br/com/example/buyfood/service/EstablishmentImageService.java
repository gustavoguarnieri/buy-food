package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.FileStorageFolder;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.repository.EstablishmentImageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class EstablishmentImageService {

  @Autowired private ModelMapper modelMapper;

  @Autowired private EstablishmentImageRepository establishmentImageRepository;

  @Autowired private FileStorageService fileStorageService;

  @Autowired private EstablishmentService establishmentService;

  @Autowired private UserService userService;

  public List<ImageResponseDTO> getEstablishmentImageList(Long establishmentId, Integer status) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    if (status == null) {
      return getEstablishmentImageListById(establishment);
    } else {
      switch (status) {
        case 1:
          return getEstablishmentImageListByIdAndStatus(establishment, RegisterStatus.ENABLED);
        case 0:
          {
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
    return saveImage(file, establishment);
  }

  private ImageResponseDTO saveImage(MultipartFile file, EstablishmentEntity establishment) {

    var downloadPath = getDownloadEstablishmentPath(establishment);

    var uploadFileResponse =
        fileStorageService.saveFile(
            file, FileStorageFolder.ESTABLISHMENTS, establishment.getId(), downloadPath);

    var imageEntity = fileStorageService.createImageEntity(establishment, uploadFileResponse);
    establishmentImageRepository.save(imageEntity);

    return fileStorageService.createImageResponseDTO(imageEntity.getId(), uploadFileResponse, 1);
  }

  public List<ImageResponseDTO> createEstablishmentImageList(
      Long establishmentId, MultipartFile[] files) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);

    var downloadPath = getDownloadEstablishmentPath(establishment);

    var uploadFileResponse =
        fileStorageService.saveFileList(
            files, FileStorageFolder.ESTABLISHMENTS, establishmentId, downloadPath);

    List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

    uploadFileResponse.forEach(
        i -> {
          var imageEntity = fileStorageService.createImageEntity(establishment, i);
          establishmentImageRepository.save(imageEntity);
          imageResponseDTOList.add(
              fileStorageService.createImageResponseDTO(imageEntity.getId(), i, 1));
        });

    return imageResponseDTOList;
  }

  public void updateEstablishmentImage(
      Long establishmentId, Long imageId, ImageRequestDTO imageRequestDto) {
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

  @Retryable(value = Exception.class, maxAttempts = 6, backoff = @Backoff(delayExpression = "200"))
  public ResponseEntity<Resource> getDownloadEstablishmentImage(
      Long establishmentId, String fileName, HttpServletRequest request) {
    return fileStorageService.downloadFile(
        FileStorageFolder.ESTABLISHMENTS, establishmentId, fileName, request);
  }

  private ImageEntity getEstablishmentImageByEstablishmentAndImage(
      Long establishmentId, Long imageId) {
    return establishmentImageRepository
        .findByIdAndEstablishmentId(imageId, establishmentId)
        .orElseThrow(() -> new NotFoundException("Establishment image not found"));
  }

  private List<ImageResponseDTO> getEstablishmentImageListById(EstablishmentEntity establishment) {
    return establishmentImageRepository.findAllByEstablishmentId(establishment.getId()).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private List<ImageResponseDTO> getEstablishmentImageListByIdAndStatus(
      EstablishmentEntity establishment, RegisterStatus enabled) {
    return establishmentImageRepository
        .findAllByEstablishmentIdAndStatus(establishment.getId(), enabled.getValue())
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private String getDownloadEstablishmentPath(EstablishmentEntity establishment) {
    return "/api/v1/establishments/" + establishment.getId() + "/images/download-file/";
  }

  private String getUserId() {
    return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
  }

  private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
    if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
      throw new ForbiddenException("User is not owner of establishment");
    }
  }

  private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
    return modelMapper.map(imageEntity, ImageResponseDTO.class);
  }

  private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
    return modelMapper.map(imageRequestDto, ImageEntity.class);
  }
}
