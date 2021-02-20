package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.BusinessHoursPutRequestDTO;
import br.com.example.buyfood.model.dto.request.BusinessHoursRequestDTO;
import br.com.example.buyfood.model.dto.response.BusinessHoursResponseDTO;
import br.com.example.buyfood.service.BusinessHoursService;
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
@RequestMapping("/api/v1/establishment/{establishmentId}/business-hours")
public class BusinessHoursController {

    @Autowired
    private BusinessHoursService businessHoursService;

    @GetMapping
    @ApiOperation(value = "Returns a list of business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of business hours",
                    response = BusinessHoursResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<BusinessHoursResponseDTO> getBusinessHoursList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam(required = false) Integer status) {
        log.info("getBusinessHoursList: starting to consult the list of business hours, " +
                "establishmentId={}", establishmentId);
        var businessHoursResponseDtoList =
                businessHoursService.getBusinessHoursList(establishmentId, status);
        log.info("getBusinessHoursList: finished to consult the list of business hours, " +
                "establishmentId={}", establishmentId);
        return businessHoursResponseDtoList;
    }

    @GetMapping("/{businessHoursId}")
    @ApiOperation(value = "Returns the informed business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed business hours",
                    response = BusinessHoursResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public BusinessHoursResponseDTO getBusinessHours(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("businessHoursId") Long businessHoursId) {
        log.info("getBusinessHours: starting to consult business hours by establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        var businessHoursResponseDto =
                businessHoursService.getBusinessHours(establishmentId, businessHoursId);
        log.info("getBusinessHours: finished to consult business hours by establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        return businessHoursResponseDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created business hours", response = BusinessHoursResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public BusinessHoursResponseDTO createBusinessHours(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @RequestBody BusinessHoursRequestDTO businessHoursRequestDto) {

        log.info("createBusinessHours: starting to create new business hours, establishmentId={}", establishmentId);
        var businessHoursResponseDto = businessHoursService
                .createBusinessHours(establishmentId, businessHoursRequestDto);
        log.info("createBusinessHours: finished to create new business hours, establishmentId={}", establishmentId);
        return businessHoursResponseDto;
    }

    @PutMapping("/{businessHoursId}")
    @ApiOperation(value = "Update business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated business hours"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateBusinessHours(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                    @Valid @NotBlank @PathVariable("businessHoursId") Long businessHoursId,
                                    @Valid @RequestBody BusinessHoursPutRequestDTO businessHoursPutRequestDto) {

        log.info("updateBusinessHours: starting update business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        businessHoursService.updateBusinessHours(establishmentId, businessHoursId, businessHoursPutRequestDto);
        log.info("updateBusinessHours: finished update business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
    }

    @DeleteMapping("/{businessHoursId}")
    @ApiOperation(value = "Delete business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted business hours"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteBusinessHours(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                    @Valid @NotBlank @PathVariable("businessHoursId") Long businessHoursId) {

        log.info("deleteBusinessHours: starting delete business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        businessHoursService.deleteBusinessHours(establishmentId, businessHoursId);
        log.info("deleteBusinessHours: finished delete business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
    }
}