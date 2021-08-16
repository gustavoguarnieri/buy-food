package br.com.example.buyfood.controller.establishment;

import br.com.example.buyfood.model.dto.request.EstablishmentCategoryRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentCategoryResponseDTO;
import br.com.example.buyfood.service.establishment.EstablishmentCategoryService;
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
@RequestMapping("/api/v1/establishments/categories")
public class EstablishmentCategoryController {

  @Autowired private EstablishmentCategoryService establishmentCategoryService;

  @GetMapping
  @ApiOperation(value = "Returns a list of establishment category")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a list of establishment category",
            response = EstablishmentCategoryResponseDTO.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<EstablishmentCategoryResponseDTO> getEstablishmentCategoryList(
      @RequestParam(required = false) Integer status) {
    log.info(
        "getEstablishmentCategoryList: starting to consult the list of establishment category");
    var establishmentCategoryResponseDtoList =
        establishmentCategoryService.getEstablishmentCategoryList(status);
    log.info("getEstablishmentCategoryList: finished to consult the list of establishment");
    return establishmentCategoryResponseDtoList;
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Returns the informed establishment category")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns the informed establishment category",
            response = EstablishmentCategoryResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public EstablishmentCategoryResponseDTO getEstablishmentCategory(
      @Valid @NotBlank @PathVariable("id") Long id) {
    log.info("getEstablishmentCategory: starting to consult establishment category by id={}", id);
    var establishmentCategoryResponseDto =
        establishmentCategoryService.getEstablishmentCategory(id);
    log.info("getEstablishmentCategory: finished to consult establishment by id={}", id);
    return establishmentCategoryResponseDto;
  }

  @Secured({"ROLE_ADMIN"})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create a new establishment category")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 201,
            message = "Created establishment category",
            response = EstablishmentCategoryResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public EstablishmentCategoryResponseDTO createEstablishmentCategory(
      @Valid @RequestBody EstablishmentCategoryRequestDTO establishmentCategoryRequestDto) {
    log.info("createEstablishmentCategory: starting to create new establishment category");
    var establishmentCategoryResponseDto =
        establishmentCategoryService.createEstablishmentCategory(establishmentCategoryRequestDto);
    log.info("createEstablishmentCategory: finished to create new establishment category");
    return establishmentCategoryResponseDto;
  }

  @Secured({"ROLE_ADMIN"})
  @PutMapping("/{id}")
  @ApiOperation(value = "Update establishment category")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Updated establishment category"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void updateEstablishmentCategory(
      @Valid @NotBlank @PathVariable("id") Long id,
      @Valid @RequestBody EstablishmentCategoryRequestDTO establishmentCategoryRequestDto) {
    log.info("updateEstablishmentCategory: starting update establishment category id={}", id);
    establishmentCategoryService.updateEstablishmentCategory(id, establishmentCategoryRequestDto);
    log.info("updateEstablishmentCategory: finished update establishment category id={}", id);
  }

  @Secured({"ROLE_ADMIN"})
  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete establishment category")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Deleted establishment category"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void deleteEstablishmentCategory(@Valid @NotBlank @PathVariable("id") Long id) {
    log.info("deleteEstablishmentCategory: starting delete establishment category id={}", id);
    establishmentCategoryService.deleteEstablishmentCategory(id);
    log.info("deleteEstablishmentCategory: finished delete establishment category id={}", id);
  }
}
