package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.DeliveryTaxPutRequestDTO;
import br.com.example.buyfood.model.dto.request.DeliveryTaxRequestDTO;
import br.com.example.buyfood.model.dto.response.DeliveryTaxResponseDTO;
import br.com.example.buyfood.service.DeliveryTaxService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/establishments/{establishmentId}/delivery-tax")
public class DeliveryTaxController {

    @Autowired
    private DeliveryTaxService deliveryTaxService;

    @GetMapping
    @ApiOperation(value = "Returns a list of delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of delivery tax",
                    response = DeliveryTaxResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thRrown"),
    })
    public List<DeliveryTaxResponseDTO> getDeliveryTaxList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam(required = false) Integer status) {
        log.info("getDeliveryTaxList: starting to consult the list of delivery tax " +
                "establishmentId={}", establishmentId);
        var deliveryTaxResponseDto =
                deliveryTaxService.getDeliveryTaxList(establishmentId, status);
        log.info("getDeliveryTaxList: finished to consult the list of delivery tax " +
                "establishmentId={}", establishmentId);
        return deliveryTaxResponseDto;
    }

    @GetMapping("/{deliveryTaxId}")
    @ApiOperation(value = "Returns the informed delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed delivery tax",
                    response = DeliveryTaxResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryTaxResponseDTO getDeliveryTax(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("deliveryTaxId") Long deliveryTaxId) {
        log.info("getDeliveryTax: starting to consult delivery tax by establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
        var deliveryTaxResponseDto =
                deliveryTaxService.getDeliveryTax(establishmentId, deliveryTaxId);
        log.info("getDeliveryTax: finished to consult delivery tax by establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
        return deliveryTaxResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created delivery tax", response = DeliveryTaxResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryTaxResponseDTO createDeliveryTax(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @RequestBody DeliveryTaxRequestDTO deliveryTaxRequestDto) {

        log.info("createDeliveryTax: starting to create new delivery tax, establishmentId={}", establishmentId);
        var deliveryTaxResponseDto = deliveryTaxService
                .createDeliveryTax(establishmentId, deliveryTaxRequestDto);
        log.info("createDeliveryTax: finished to create new delivery tax, establishmentId={}", establishmentId);
        return deliveryTaxResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PutMapping("/{deliveryTaxId}")
    @ApiOperation(value = "Update delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated delivery tax"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateDeliveryTax(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                  @Valid @NotBlank @PathVariable("deliveryTaxId") Long deliveryTaxId,
                                  @Valid @RequestBody DeliveryTaxPutRequestDTO deliveryTaxPutRequestDto) {

        log.info("updateDeliveryTax: starting update delivery tax establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
        deliveryTaxService.updateDeliveryTax(establishmentId, deliveryTaxId, deliveryTaxPutRequestDto);
        log.info("updateDeliveryTax: finished update delivery tax establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @DeleteMapping("/{deliveryTaxId}")
    @ApiOperation(value = "Delete delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted delivery tax"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteDeliveryTax(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                  @Valid @NotBlank @PathVariable("deliveryTaxId") Long deliveryTaxId) {
        log.info("deleteDeliveryTax: starting delete delivery tax establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
        deliveryTaxService.deleteDeliveryTax(establishmentId, deliveryTaxId);
        log.info("deleteDeliveryTax: finished delete delivery tax establishmentId={}, deliveryTaxId={}",
                establishmentId, deliveryTaxId);
    }
}