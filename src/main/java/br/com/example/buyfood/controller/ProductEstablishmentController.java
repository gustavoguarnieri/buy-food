package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.ProductRequestDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.service.ProductEstablishmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/{establishmentId}/products")
public class ProductEstablishmentController {

  @Autowired private ProductEstablishmentService productEstablishmentService;

  @GetMapping
  @ApiOperation(value = "Returns a list of product")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of product",
            response = ProductResponseDTO.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<ProductResponseDTO> getProductListByEstablishment(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @RequestParam(required = false) Integer status) {
    log.info(
        "getProductList: starting to consult the list of product by establishmentId={}",
        establishmentId);
    var productResponseDtoList =
        productEstablishmentService.getProductListByEstablishment(establishmentId, status);
    log.info(
        "getProductList: finished to consult the list of product by establishmentId={}",
        establishmentId);
    return productResponseDtoList;
  }

  @GetMapping("/{productId}")
  @ApiOperation(value = "Returns the informed product")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns the informed product",
            response = ProductResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public ProductResponseDTO getProduct(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId) {
    log.info(
        "getProduct: starting to consult product by establishmentId={}, productId={}",
        establishmentId,
        productId);
    var productResponseDto = productEstablishmentService.getProduct(establishmentId, productId);
    log.info(
        "getProduct: finished to consult product by establishmentId={}, productId={}",
        establishmentId,
        productId);
    return productResponseDto;
  }

  @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create a new product")
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Created product", response = ProductResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public ProductResponseDTO createProduct(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @RequestBody ProductRequestDTO productRequestDto) {
    log.info("createProduct: starting to create new product establishmentId={}", establishmentId);
    var productResponseDto =
        productEstablishmentService.createProduct(establishmentId, productRequestDto);
    log.info("createProduct: finished to create new product establishmentId={}", establishmentId);
    return productResponseDto;
  }

  @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
  @PutMapping("/{productId}")
  @ApiOperation(value = "Update product")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Updated product"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void updateProduct(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @Valid @RequestBody ProductRequestDTO productRequestDto) {
    log.info(
        "updateProduct: starting update product establishmentId={}, productId={}",
        establishmentId,
        productId);
    productEstablishmentService.updateProduct(establishmentId, productId, productRequestDto);
    log.info(
        "updateProduct: finished update product establishmentId={}, productId={}",
        establishmentId,
        productId);
  }

  @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
  @DeleteMapping("/{productId}")
  @ApiOperation(value = "Delete product")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Deleted product"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void deleteProduct(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId) {
    log.info(
        "deleteProduct: starting delete product establishmentId={}, productId={}",
        establishmentId,
        productId);
    productEstablishmentService.deleteProduct(establishmentId, productId);
    log.info(
        "deleteProduct: finished delete product establishmentId={}, productId={}",
        establishmentId,
        productId);
  }
}
