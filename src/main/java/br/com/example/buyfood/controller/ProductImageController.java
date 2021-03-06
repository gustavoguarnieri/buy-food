package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.service.ProductImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/{establishmentId}/products/{productId}/images")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping
    @ApiOperation(value = "Returns a list of product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of product image",
                    response = ProductResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDTO> getProductImageList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("productId") Long productId,
            @RequestParam(required = false) Integer status) {
        log.info("getProductImageList: starting to consult the list of product image, establishmentId={}, productId={}",
                establishmentId, productId);
        var imageResponseDto = productImageService.getProductImageList(establishmentId, productId, status);
        log.info("getProductImageList: finished to consult the list of product image, establishmentId={}, productId={}",
                establishmentId, productId);
        return imageResponseDto;
    }

    @GetMapping("/{imageId}")
    @ApiOperation(value = "Returns the informed product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed product image",
                    response = ProductResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDTO getProductImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                            @Valid @NotBlank @PathVariable("productId") Long productId,
                                            @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("getProductImage: starting to consult product image by establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
        var imageResponseDto = productImageService.getProductImage(establishmentId, productId, imageId);
        log.info("getProductImage: finished to consult product image by establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
        return imageResponseDto;
    }

    @GetMapping("/download-file/{fileName}")
    @ApiOperation(value = "Returns the informed download product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed download product image",
                    response = ProductResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ResponseEntity<Resource> getDownloadProductImageList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("productId") Long productId,
            @Valid @NotBlank @PathVariable("fileName") String fileName,
            HttpServletRequest request) {
        log.info("getDownloadProductImageList: starting to consult product image by " +
                "establishmentId={}, productId={}, fileName={}", establishmentId, productId, fileName);
        var imageResourceResponse =
                productImageService.getDownloadProductImage(productId, fileName, request);
        log.info("getDownloadProductImageList: finished to consult product image by " +
                "establishmentId={}, productId={}, fileName={}", establishmentId, productId, fileName);
        return imageResourceResponse;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping("/upload-file")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new product image")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created product image", response = ImageResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDTO createProductImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                               @Valid @NotBlank @PathVariable("productId") Long productId,
                                               @RequestParam MultipartFile file) {
        log.info("createProductImage: starting to create new product image, establishmentId={}, productId={}",
                establishmentId, productId);
        var productImageResponseDto =
                productImageService.createProductImage(establishmentId, productId, file);
        log.info("createProductImage: finished to create new product image, establishmentId={}, productId={}",
                establishmentId, productId);
        return productImageResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping("/upload-files")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new product images")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created product images", response = ProductResponseDTO.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDTO> createProductImages(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("productId") Long productId,
            @RequestParam MultipartFile[] files) {
        log.info("createProductImages: starting to create new product images, establishmentId={}, productId={}",
                establishmentId, productId);
        var imageResponseDtoList = productImageService
                .createProductImageList(establishmentId, productId, files);
        log.info("createProductImages: finished to create new product images, establishmentId={}, productId={}",
                establishmentId, productId);
        return imageResponseDtoList;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PutMapping("/{imageId}")
    @ApiOperation(value = "Update product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated product image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateProductImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                   @Valid @NotBlank @PathVariable("productId") Long productId,
                                   @Valid @NotBlank @PathVariable("imageId") Long imageId,
                                   @Valid @RequestBody ImageRequestDTO imageRequestDto) {
        log.info("updateProductImage: starting update product image by establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
        productImageService.updateProductImage(establishmentId, productId, imageId, imageRequestDto);
        log.info("updateProductImage: finished update product image by establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @DeleteMapping("/{imageId}")
    @ApiOperation(value = "Delete product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted product image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteProductImage(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                   @Valid @NotBlank @PathVariable("productId") Long productId,
                                   @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("deleteProductImage: starting delete product image establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
        productImageService.deleteProductImage(establishmentId, productId, imageId);
        log.info("deleteProductImage: finished delete product image establishmentId={}, productId={}, imageId={}",
                establishmentId, productId, imageId);
    }
}