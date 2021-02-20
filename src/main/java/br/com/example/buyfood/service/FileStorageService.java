package br.com.example.buyfood.service;

import br.com.example.buyfood.config.Property.FileStorageProperty;
import br.com.example.buyfood.exception.FileStorageException;
import br.com.example.buyfood.exception.FileNotFoundException;
import br.com.example.buyfood.model.dto.response.UploadFileResponseDTO;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileStorageService {

    private Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperty fileStorageProperty) {
        getFileStorageLocation(fileStorageProperty);
    }

    private void getFileStorageLocation(FileStorageProperty fileStorageProperty) {
        fileStorageLocation = Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    public UploadFileResponseDTO saveFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            isValidFilePath(fileName);
            copyFileToTargetLocation(file, fileName);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }

        String fileDownloadUri = fileDownloadURI(fileName);

        return new UploadFileResponseDTO(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    public List<UploadFileResponseDTO> saveFileList(MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::saveFile)
                .collect(Collectors.toList());
    }

    private String fileDownloadURI(String fileName) {
        String downloadFileEndpoint = "download-file";
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/" + downloadFileEndpoint + "/")
                .path(fileName)
                .toUriString();
    }

    private void copyFileToTargetLocation(MultipartFile file, String fileName) throws IOException {
        Path targetLocation = fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private void isValidFilePath(String fileName) {
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            return getResource(fileName);
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    private Resource getResource(String fileName) throws MalformedURLException {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    public ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request) {
        Resource resource = loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        resource.getFilename() + "\"")
                .body(resource);
    }
}