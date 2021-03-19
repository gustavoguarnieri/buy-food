package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.service.EstablishmentImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/{establishmentId}/images")
public class EstablishmentImageController {

    @Autowired
    private EstablishmentImageService establishmentImageService;

    @GetMapping
    @ApiOperation(value = "Returns a list of establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of establishment image",
                    response = ImageResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDTO> getEstablishmentImageList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam(required = false) Integer status) {
        log.info("getEstablishmentImageList: starting to consult the list of establishment image, establishmentId={}",
                establishmentId);
        var imageResponseDto =
                establishmentImageService.getEstablishmentImageList(establishmentId, status);
        log.info("getEstablishmentImageList: finished to consult the list of establishment image, establishmentId={}",
                establishmentId);
        return imageResponseDto;
    }

    @GetMapping("/{imageId}")
    @ApiOperation(value = "Returns the informed establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed establishment image",
                    response = ImageResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDTO getEstablishmentImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                                  @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("getEstablishmentImage: starting to consult establishment image by establishmentId={}, imageId={}",
                establishmentId, imageId);
        var imageResponseDto =
                establishmentImageService.getEstablishmentImage(establishmentId, imageId);
        log.info("getEstablishmentImage: finished to consult establishment image by establishmentId={}, imageId={}",
                establishmentId, imageId);
        return imageResponseDto;
    }

    @GetMapping("/download-file/{fileName}")
    @ApiOperation(value = "Returns the informed download establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed download establishment image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ResponseEntity<Resource> getDownloadEstablishmentImageList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("fileName") String fileName,
            HttpServletRequest request) {
        log.info("getDownloadEstablishmentImageList: starting to consult establishment image by " +
                "establishmentId={}, fileName={}", establishmentId, fileName);
        var imageResourceResponse =
                establishmentImageService.getDownloadEstablishmentImage(establishmentId, fileName, request);
        log.info("getDownloadEstablishmentImageList: finished to consult establishment image by " +
                "establishmentId={}, fileName={}", establishmentId, fileName);
        return imageResourceResponse;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping("/upload-file")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created establishment image", response = ImageResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDTO createEstablishmentImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                                     @RequestParam MultipartFile file) {
        log.info("createEstablishmentImage: starting to create new establishment image, establishmentId={}",
                establishmentId);
        var productImageResponseDto = establishmentImageService
                .createEstablishmentImage(establishmentId, file);
        log.info("createEstablishmentImage: finished to create new establishment image, establishmentId={}",
                establishmentId);
        return productImageResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping("/upload-files")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new establishment images")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created establishment images", response = ImageResponseDTO.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDTO> createEstablishmentImages(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam MultipartFile[] files) {
        log.info("createEstablishmentImages: starting to create new establishment images, establishmentId={}",
                establishmentId);
        var imageResponseDtoList = establishmentImageService
                .createEstablishmentImageList(establishmentId, files);
        log.info("createEstablishmentImages: finished to create new establishment images, establishmentId={}",
                establishmentId);
        return imageResponseDtoList;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PutMapping("/{imageId}")
    @ApiOperation(value = "Update establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated establishment image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateEstablishmentImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                         @Valid @NotBlank @PathVariable("imageId") Long imageId,
                                         @Valid @RequestBody ImageRequestDTO imageRequestDto) {
        log.info("updateEstablishmentImage: starting update establishment image by establishmentId={}, imageId={}",
                establishmentId, imageId);
        establishmentImageService.updateEstablishmentImage(establishmentId, imageId, imageRequestDto);
        log.info("updateEstablishmentImage: finished update establishment image by establishmentId={}, imageId={}",
                establishmentId, imageId);
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @DeleteMapping("/{imageId}")
    @ApiOperation(value = "Delete establishment image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted establishment image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteEstablishmentImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                         @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("deleteEstablishmentImage: starting delete establishment image establishmentId={}, imageId={}",
                establishmentId, imageId);
        establishmentImageService.deleteEstablishmentImage(establishmentId, imageId);
        log.info("deleteEstablishmentImage: finished delete establishment image establishmentId={}, imageId={}",
                establishmentId, imageId);
    }
}