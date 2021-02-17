package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.ProductRequestDto;
import br.com.example.buyfood.model.dto.response.ProductResponseDto;
import br.com.example.buyfood.service.ProductService;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/establishment/{establishmentId}/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @ApiOperation(value = "Returns a list of product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of product",
                    response = ProductResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<ProductResponseDto> getProductList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam(required = false) Integer status) {
        log.info("getProductList: starting to consult the list of product establishmentId={}", establishmentId);
        var productResponseDtoList = productService.getProductList(establishmentId, status);
        log.info("getProductList: finished to consult the list of product establishmentId={}", establishmentId);
        return productResponseDtoList;
    }

    @GetMapping("/{productId}")
    @ApiOperation(value = "Returns the informed product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed product",
                    response = ProductResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ProductResponseDto getProduct(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("productId") Long productId) {
        log.info("getProduct: starting to consult product by establishmentId={}, productId={}",
                establishmentId, productId);
        var productResponseDto = productService.getProduct(establishmentId, productId);
        log.info("getProduct: finished to consult product by establishmentId={}, productId={}",
                establishmentId, productId);
        return productResponseDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created product", response = ProductResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ProductResponseDto createProduct(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("createProduct: starting to create new product establishmentId={}", establishmentId);
        var productResponseDto = productService
                .createProduct(establishmentId, productRequestDto);
        log.info("createProduct: finished to create new product establishmentId={}", establishmentId);
        return productResponseDto;
    }

    @PutMapping("/{productId}")
    @ApiOperation(value = "Update product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated product"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateProduct(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                              @Valid @NotBlank @PathVariable("productId") Long productId,
                              @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("updateProduct: starting update product establishmentId={}, productId={}",
                establishmentId, productId);
        productService.updateProduct(establishmentId, productId, productRequestDto);
        log.info("updateProduct: finished update product establishmentId={}, productId={}",
                establishmentId, productId);
    }

    @DeleteMapping("/{productId}")
    @ApiOperation(value = "Delete product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted product"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteProduct(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                              @Valid @NotBlank @PathVariable("productId") Long productId) {
        log.info("deleteProduct: starting delete product establishmentId={}, productId={}",
                establishmentId, productId);
        productService.deleteProduct(establishmentId, productId);
        log.info("deleteProduct: finished delete product establishmentId={}, productId={}",
                establishmentId, productId);
    }
}