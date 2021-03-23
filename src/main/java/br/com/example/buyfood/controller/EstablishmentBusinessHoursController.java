package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursPutRequestDTO;
import br.com.example.buyfood.model.dto.request.EstablishmentBusinessHoursRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentBusinessHoursResponseDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentResponseDTO;
import br.com.example.buyfood.service.EstablishmentBusinessHoursService;
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
@RequestMapping("/api/v1/establishments")
public class EstablishmentBusinessHoursController {

    @Autowired
    private EstablishmentBusinessHoursService establishmentBusinessHoursService;

    @GetMapping("/{establishmentId}/business-hours")
    @ApiOperation(value = "Returns a list of business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of business hours",
                    response = EstablishmentBusinessHoursResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<EstablishmentBusinessHoursResponseDTO> getBusinessHoursList(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @RequestParam(required = false) Integer status) {
        log.info("getBusinessHoursList: starting to consult the list of business hours, " +
                "establishmentId={}", establishmentId);
        var businessHoursResponseDtoList =
                establishmentBusinessHoursService.getBusinessHoursList(establishmentId, status);
        log.info("getBusinessHoursList: finished to consult the list of business hours, " +
                "establishmentId={}", establishmentId);
        return businessHoursResponseDtoList;
    }

    @GetMapping("/{establishmentId}/business-hours/{businessHoursId}")
    @ApiOperation(value = "Returns the informed business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed business hours",
                    response = EstablishmentBusinessHoursResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public EstablishmentBusinessHoursResponseDTO getBusinessHours(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @NotBlank @PathVariable("businessHoursId") Long businessHoursId) {
        log.info("getBusinessHours: starting to consult business hours by establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        var businessHoursResponseDto =
                establishmentBusinessHoursService.getBusinessHours(establishmentId, businessHoursId);
        log.info("getBusinessHours: finished to consult business hours by establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        return businessHoursResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @GetMapping("/business-hours/mine")
    @ApiOperation(value = "Returns my business-hours establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns my business-hours establishment",
                    response = EstablishmentResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<EstablishmentBusinessHoursResponseDTO> getMyBusinessHoursList(@RequestParam(required = false) Integer status) {
        log.info("getMyBusinessHoursList: starting to consult my business hours");
        var businessHoursResponseDtoList =
                establishmentBusinessHoursService.getMyBusinessHoursList(status);
        log.info("getMyBusinessHoursList: finished to consult my business hours");
        return businessHoursResponseDtoList;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping("/{establishmentId}/business-hours")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created business hours", response = EstablishmentBusinessHoursResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public EstablishmentBusinessHoursResponseDTO createBusinessHours(
            @Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
            @Valid @RequestBody EstablishmentBusinessHoursRequestDTO establishmentBusinessHoursRequestDto) {

        log.info("createBusinessHours: starting to create new business hours, establishmentId={}", establishmentId);
        var businessHoursResponseDto = establishmentBusinessHoursService
                .createBusinessHours(establishmentId, establishmentBusinessHoursRequestDto);
        log.info("createBusinessHours: finished to create new business hours, establishmentId={}", establishmentId);
        return businessHoursResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PutMapping("/{establishmentId}/business-hours/{businessHoursId}")
    @ApiOperation(value = "Update business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated business hours"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateBusinessHours(@Valid @NotBlank @PathVariable("establishmentId") Long establishmentId,
                                    @Valid @NotBlank @PathVariable("businessHoursId") Long businessHoursId,
                                    @Valid @RequestBody EstablishmentBusinessHoursPutRequestDTO establishmentBusinessHoursPutRequestDto) {

        log.info("updateBusinessHours: starting update business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
        establishmentBusinessHoursService.updateBusinessHours(establishmentId, businessHoursId, establishmentBusinessHoursPutRequestDto);
        log.info("updateBusinessHours: finished update business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @DeleteMapping("/{establishmentId}/business-hours/{businessHoursId}")
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
        establishmentBusinessHoursService.deleteBusinessHours(establishmentId, businessHoursId);
        log.info("deleteBusinessHours: finished delete business hours establishmentId={}, businessHoursId={}",
                establishmentId, businessHoursId);
    }
}