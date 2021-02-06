package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.DeliveryTaxRequestDto;
import br.com.example.buyfood.model.dto.response.DeliveryTaxResponseDto;
import br.com.example.buyfood.service.DeliveryTaxService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery-tax")
public class DeliveryTaxController {

    @Autowired
    private DeliveryTaxService deliveryTaxService;

    @GetMapping
    @ApiOperation(value = "Returns a list of delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of delivery tax",
                    response = DeliveryTaxRequestDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thRrown"),
    })
    public List<DeliveryTaxResponseDto> getDeliveryTaxList() {
        log.info("getDeliveryTaxList: starting to consult the list of delivery tax");
        var  deliveryTaxResponseDto = deliveryTaxService.getDeliveryTaxList();
        log.info("getDeliveryTaxList: finished to consult the list of delivery tax");
        return deliveryTaxResponseDto;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed delivery tax",
                    response = DeliveryTaxRequestDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryTaxResponseDto getDeliveryTax(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("getDeliveryTax: starting to consult delivery tax by id={}", id);
        var deliveryTaxResponseDto = deliveryTaxService.getDeliveryTax(id);
        log.info("getDeliveryTax: finished to consult delivery tax by id={}", id);
        return deliveryTaxResponseDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created delivery tax", response = DeliveryTaxRequestDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryTaxResponseDto createDeliveryTax(
            @Valid @RequestBody DeliveryTaxRequestDto deliveryTaxRequestDto) {

        log.info("createDeliveryTax: starting to create new delivery tax");
        var deliveryTaxResponseDto = deliveryTaxService
                .createDeliveryTax(deliveryTaxRequestDto);
        log.info("createDeliveryTax: finished to create new delivery tax");
        return deliveryTaxResponseDto;
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated delivery tax"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateDeliveryTax(@Valid @NotBlank @PathVariable("id") Long id,
                                    @Valid @RequestBody DeliveryTaxRequestDto deliveryTaxRequestDto) {

        log.info("updateDeliveryTax: starting update delivery tax id={}", id);
        deliveryTaxService.updateDeliveryTax(id, deliveryTaxRequestDto);
        log.info("updateDeliveryTax: finished update delivery tax id={}", id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete delivery tax")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted delivery tax"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteDeliveryTax(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("deleteDeliveryTax: starting delete delivery tax id={}", id);
        deliveryTaxService.deleteDeliveryTax(id);
        log.info("deleteDeliveryTax: finished delete delivery tax id={}", id);
    }
}