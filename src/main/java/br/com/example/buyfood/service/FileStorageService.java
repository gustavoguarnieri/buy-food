package br.com.example.buyfood.service;

import br.com.example.buyfood.config.Property.FileStorageProperty;
import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.FileStorageFolder;
import br.com.example.buyfood.exception.FileNotFoundException;
import br.com.example.buyfood.exception.FileStorageException;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.dto.response.UploadFileResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Service
public class FileStorageService {

  private Path fileStorageLocation;
  private final FileStorageProperty fileStorageProperty;

  @Autowired
  public FileStorageService(FileStorageProperty fileStorageProperty) {
    this.fileStorageProperty = fileStorageProperty;
    getFileStorageLocation(fileStorageProperty);
  }

  private void getFileStorageLocation(
      FileStorageProperty fileStorageProperty, FileStorageFolder fileStorageFolder, Long id) {
    fileStorageLocation =
        Paths.get(fileStorageProperty.getUploadDir() + fileStorageFolder.getValue() + id)
            .toAbsolutePath()
            .normalize();
    createDirectories();
  }

  private void getFileStorageLocation(FileStorageProperty fileStorageProperty) {
    fileStorageLocation =
        Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();
    createDirectories();
  }

  private void createDirectories() {
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException(ErrorMessages.COULD_NOT_CREATE_DIRECTORY);
    }
  }

  public UploadFileResponseDTO saveFile(
      MultipartFile file, FileStorageFolder fileStorageFolder, Long id, String fileUri) {
    var uuidImage = UUID.randomUUID();

    var extension = FilenameUtils.getExtension(Objects.requireNonNull(file.getOriginalFilename()));

    var fileName = StringUtils.cleanPath(uuidImage + "." + extension);

    getFileStorageLocation(fileStorageProperty, fileStorageFolder, id);

    try {
      isValidFilePath(fileName);
      copyFileToTargetLocation(file, fileName);
    } catch (IOException ex) {
      throw new FileStorageException(ErrorMessages.COULD_NOT_STORE_FILE + " filename= " + fileName);
    }

    var fileDownloadUri = fileDownloadURI(fileUri, fileName);

    return new UploadFileResponseDTO(
        fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  public List<UploadFileResponseDTO> saveFileList(
      MultipartFile[] files, FileStorageFolder fileStorageFolder, Long id, String fileUri) {
    return Arrays.stream(files)
        .map(i -> saveFile(i, fileStorageFolder, id, fileUri))
        .collect(Collectors.toList());
  }

  private String fileDownloadURI(String fileUri, String fileName) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/" + fileUri + "/")
        .path(fileName)
        .toUriString();
  }

  private void copyFileToTargetLocation(MultipartFile file, String fileName) throws IOException {
    var targetLocation = fileStorageLocation.resolve(fileName);
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
  }

  private void isValidFilePath(String fileName) {
    if (fileName.contains("..")) {
      throw new FileStorageException(
          ErrorMessages.INVALID_FILE_NAME_PATH + " filename= " + fileName);
    }
  }

  public Resource loadFileAsResource(
      FileStorageFolder fileStorageFolder, Long id, String fileName) {
    try {
      return getResource(fileStorageFolder, id, fileName);
    } catch (MalformedURLException ex) {
      throw new FileNotFoundException(ErrorMessages.FILE_NOT_FOUND + " filename= " + fileName);
    }
  }

  private Resource getResource(FileStorageFolder fileStorageFolder, Long id, String fileName)
      throws MalformedURLException {
    getFileStorageLocation(fileStorageProperty, fileStorageFolder, id);
    var filePath = fileStorageLocation.resolve(fileName).normalize();
    Resource resource = new UrlResource(filePath.toUri());

    if (resource.exists()) {
      return resource;
    } else {
      throw new FileNotFoundException(ErrorMessages.FILE_NOT_FOUND + " filename= " + fileName);
    }
  }

  public ResponseEntity<Resource> downloadFile(
      FileStorageFolder fileStorageFolder, Long id, String fileName, HttpServletRequest request) {
    var resource = loadFileAsResource(fileStorageFolder, id, fileName);

    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      log.warn(ErrorMessages.COULD_NOT_DETERMINE_FILE_TYPE);
    }

    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

  public ImageEntity createImageEntity(
      EstablishmentEntity establishment, UploadFileResponseDTO uploadFileResponse) {
    return new ImageEntity(
        establishment,
        uploadFileResponse.getFileName(),
        uploadFileResponse.getFileUri(),
        uploadFileResponse.getFileType(),
        uploadFileResponse.getSize());
  }

  public ImageEntity createImageEntity(
      ProductEntity product, UploadFileResponseDTO uploadFileResponse) {
    return new ImageEntity(
        product,
        uploadFileResponse.getFileName(),
        uploadFileResponse.getFileUri(),
        uploadFileResponse.getFileType(),
        uploadFileResponse.getSize());
  }

  public ImageResponseDTO createImageResponseDTO(
      Long id, UploadFileResponseDTO uploadFileResponse, int status) {
    return new ImageResponseDTO(
        id,
        uploadFileResponse.getFileName(),
        uploadFileResponse.getFileUri(),
        uploadFileResponse.getFileType(),
        uploadFileResponse.getSize(),
        status);
  }
}
