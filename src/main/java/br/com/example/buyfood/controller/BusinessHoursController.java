package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.BusinessHoursRequestDto;
import br.com.example.buyfood.model.dto.response.BusinessHoursResponseDto;
import br.com.example.buyfood.service.BusinessHoursService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/business-hours")
public class BusinessHoursController {

    private final BusinessHoursService businessHoursService;

    public BusinessHoursController(BusinessHoursService businessHoursService) {
        this.businessHoursService = businessHoursService;
    }

    @GetMapping
    @ApiOperation(value = "Returns a list of business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of business hours",
                    response = BusinessHoursResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<BusinessHoursResponseDto> getBusinessHoursList() {

        log.info("getBusinessHoursList: starting to consult the list of business hours");

        var  businessHoursResponseDtoList = businessHoursService.getBusinessHoursList();

        log.info("getBusinessHoursList: finished to consult the list of business hours");

        return businessHoursResponseDtoList;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed business hours",
                    response = BusinessHoursResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public BusinessHoursResponseDto  getBusinessHours(@Valid @NotBlank @PathVariable("id") Long id) {

        log.info("getBusinessHours: starting to consult business hours by id={}", id);

        var businessHoursResponseDto = businessHoursService.getBusinessHours(id);

        log.info("getBusinessHours: finished to consult business hours by id={}", id);

        return businessHoursResponseDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created business hours", response = BusinessHoursResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public BusinessHoursResponseDto createBusinessHours(
            @Valid @RequestBody BusinessHoursRequestDto businessHoursRequestDto) {

        log.info("createBusinessHours: starting to create new business hours");

        var businessHoursResponseDto = businessHoursService
                .createBusinessHours(businessHoursRequestDto);

        log.info("createBusinessHours: finished to create new business hours");

        return businessHoursResponseDto;
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated business hours"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateBusinessHours(@Valid @NotBlank @PathVariable("id") Long id,
                                    @Valid @RequestBody BusinessHoursRequestDto businessHoursRequestDto) {

        log.info("updateBusinessHours: starting update business hours id={}", id);

        businessHoursService.updateBusinessHours(id, businessHoursRequestDto);

        log.info("updateBusinessHours: finished update business hours id={}", id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete business hours")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted business hours"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteBusinessHours(@Valid @NotBlank @PathVariable("id") Long id) {

        log.info("deleteBusinessHours: starting delete business hours id={}", id);

        businessHoursService.deleteBusinessHours(id);

        log.info("deleteBusinessHours: finished delete business hours id={}", id);
    }
}