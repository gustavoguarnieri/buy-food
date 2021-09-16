package br.com.example.buyfood.controller.establishment;

import br.com.example.buyfood.model.dto.request.IngredientRequestDTO;
import br.com.example.buyfood.model.dto.response.IngredientResponseDTO;
import br.com.example.buyfood.service.establishment.EstablishmentIngredientService;
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
@RequestMapping("/api/v1/establishments/{establishmentId}/products/{productId}/ingredients")
public class EstablishmentIngredientController {

  @Autowired private EstablishmentIngredientService ingredientService;

  @GetMapping
  @ApiOperation(value = "Returns a ingredient list of product")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns a ingredient list of product",
            response = IngredientResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public List<IngredientResponseDTO> getIngredientList(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @RequestParam(required = false) Integer status) {
    log.info(
        "getIngredientList: starting to consult the ingredient list establishmentId={} productId={}, "
            + "status={}",
        establishmentId,
        productId,
        status);
    var ingredientResponseDtoList =
        ingredientService.getIngredientList(establishmentId, productId, status);
    log.info(
        "getIngredientList: finished to consult the ingredient list establishmentId={}, productId={}, "
            + "status={}",
        establishmentId,
        productId,
        status);
    return ingredientResponseDtoList;
  }

  @GetMapping("/{ingredientId}")
  @ApiOperation(value = "Returns the informed ingredient")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Returns the informed ingredient",
            response = IngredientResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public IngredientResponseDTO getIngredient(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @Valid @NotBlank @PathVariable("ingredientId") Long ingredientId) {
    log.info(
        "getIngredient: starting to consult ingredient by establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
    var ingredientResponseDto =
        ingredientService.getIngredient(establishmentId, productId, ingredientId);
    log.info(
        "getIngredient: finished to consult ingredient by establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
    return ingredientResponseDto;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create a new ingredient")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 201,
            message = "Created ingredient",
            response = IngredientResponseDTO.class),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public IngredientResponseDTO createIngredient(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @Valid @RequestBody IngredientRequestDTO ingredientRequestDTO) {
    log.info(
        "createIngredient: starting to create new ingredient by establishmentId={}, productId={}",
        establishmentId,
        productId);
    var ingredientResponseDto =
        ingredientService.createIngredient(establishmentId, productId, ingredientRequestDTO);
    log.info(
        "createIngredient: finished to create new ingredient by establishmentId={}, productId={}",
        establishmentId,
        productId);
    return ingredientResponseDto;
  }

  @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
  @PutMapping("/{ingredientId}")
  @ApiOperation(value = "Update ingredient")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Updated ingredient"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void updateIngredient(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @Valid @NotBlank @PathVariable("ingredientId") Long ingredientId,
      @Valid @RequestBody IngredientRequestDTO ingredientRequestDTO) {
    log.info(
        "updateIngredient: starting update ingredient establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
    ingredientService.updateIngredient(
        establishmentId, productId, ingredientId, ingredientRequestDTO);
    log.info(
        "updateIngredient: finished update ingredient establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
  }

  @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
  @DeleteMapping("/{ingredientId}")
  @ApiOperation(value = "Delete ingredient")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Deleted ingredient"),
        @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
        @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
        @ApiResponse(code = 500, message = "An exception was thrown"),
      })
  public void deleteIngredient(
      @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
      @Valid @NotBlank @PathVariable("productId") Long productId,
      @Valid @NotBlank @PathVariable("ingredientId") Long ingredientId) {
    log.info(
        "deleteIngredient: starting delete ingredient establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
    ingredientService.deleteIngredient(establishmentId, ingredientId);
    log.info(
        "deleteIngredient: finished delete ingredient establishmentId={}, productId={}, ingredientId={}",
        establishmentId,
        productId,
        ingredientId);
  }
}
