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
@RequestMapping("/api/v1/products")
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
    public List<ProductResponseDto> getProductList(@RequestParam(required = false) Integer status) {
        log.info("getProductList: starting to consult the list of product");
        var productResponseDtoList = productService.getProductList(status);
        log.info("getProductList: finished to consult the list of product");
        return productResponseDtoList;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed product",
                    response = ProductResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public ProductResponseDto getProduct(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("getProduct: starting to consult product by id={}", id);
        var productResponseDto = productService.getProduct(id);
        log.info("getProduct: finished to consult product by id={}", id);
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
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("createProduct: starting to create new product");
        var productResponseDto = productService
                .createProduct(productRequestDto);
        log.info("createProduct: finished to create new product");
        return productResponseDto;
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated product"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateProduct(@Valid @NotBlank @PathVariable("id") Long id,
                                    @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("updateProduct: starting update product id={}", id);
        productService.updateProduct(id, productRequestDto);
        log.info("updateProduct: finished update product id={}", id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted product"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteProduct(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("deleteProduct: starting delete product id={}", id);
        productService.deleteProduct(id);
        log.info("deleteProduct: finished delete product id={}", id);
    }
}