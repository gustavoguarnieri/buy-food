package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.ImageRequestDto;
import br.com.example.buyfood.model.dto.response.ImageResponseDto;
import br.com.example.buyfood.model.dto.response.ProductResponseDto;
import br.com.example.buyfood.service.ProductImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products/{productId}")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/images")
    @ApiOperation(value = "Returns a list of product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of product image",
                    response = ProductResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDto> getProductImageList(@Valid @NotBlank @PathVariable("productId") Long productId,
                                                      @RequestParam(required = false) Integer status) {
        log.info("getProductImageList: starting to consult the list of product image");
        var imageResponseDto = productImageService.getProductImageList(productId, status);
        log.info("getProductImageList: finished to consult the list of product image");
        return imageResponseDto;
    }

    @GetMapping("/images/{imageId}")
    @ApiOperation(value = "Returns the informed product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed product image",
                    response = ProductResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDto getProductImage(@Valid @NotBlank @PathVariable("productId") Long productId,
                                            @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("getProductImage: starting to consult product image by productId={}, imageId={}", productId, imageId);
        var imageResponseDto = productImageService.getProductImage(productId, imageId);
        log.info("getProductImage: finished to consult product image by productId={}, imageId={}", productId, imageId);
        return imageResponseDto;
    }

    @PostMapping("/images/upload-file")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new product image")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created product image", response = ImageResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ImageResponseDto createProductImage(@Valid @NotBlank @PathVariable("productId") Long productId,
                                               @RequestParam MultipartFile file) {
        log.info("createProductImage: starting to create new product image");
        var productImageResponseDto = productImageService
                .createProductImage(productId, file);
        log.info("createProductImage: finished to create new product image");
        return productImageResponseDto;
    }

    @PostMapping("/images/upload-files")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new product images")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created product images", response = ProductResponseDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ImageResponseDto> createProductImages(@Valid @NotBlank @PathVariable("productId") Long productId,
                                                      @RequestParam MultipartFile[] files) {
        log.info("createProductImages: starting to create new product images");
        var imageResponseDtoList = productImageService
                .createProductImageList(productId, files);
        log.info("createProductImages: finished to create new product images");
        return imageResponseDtoList;
    }

    @PutMapping("/images/{imageId}")
    @ApiOperation(value = "Update product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated product image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateProductImage(@Valid @NotBlank @PathVariable("productId") Long productId,
                                   @Valid @NotBlank @PathVariable("imageId") Long imageId,
                                   @Valid @RequestBody ImageRequestDto imageRequestDto) {
        log.info("updateProduct: starting update product image by productId={}, imageId={}", productId, imageId);
        productImageService.updateProductImage(productId, imageId, imageRequestDto);
        log.info("updateProduct: finished update product image by productId={}, imageId={}", productId, imageId);
    }

    @DeleteMapping("/images/{imageId}")
    @ApiOperation(value = "Delete product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted product image"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteProductImage(@Valid @NotBlank @PathVariable("productId") Long productId,
                                   @Valid @NotBlank @PathVariable("imageId") Long imageId) {
        log.info("deleteProductImage: starting delete product image productId={}, imageId={}", productId, imageId);
        productImageService.deleteProductImage(productId, imageId);
        log.info("deleteProductImage: finished delete product image productId={}, imageId={}", productId, imageId);
    }
}