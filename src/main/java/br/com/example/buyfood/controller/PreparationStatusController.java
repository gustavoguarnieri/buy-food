package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.PreparationStatusRequestDTO;
import br.com.example.buyfood.model.dto.response.PreparationStatusResponseDTO;
import br.com.example.buyfood.service.PreparationStatusService;
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
@RequestMapping("/api/v1/establishments/preparation-status")
public class PreparationStatusController {

    @Autowired
    private PreparationStatusService preparationStatusService;

    @GetMapping
    @ApiOperation(value = "Returns a list of preparation status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of preparation status",
                    response = PreparationStatusResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<PreparationStatusResponseDTO> getPreparationStatusList(
            @RequestParam(required = false) Integer status
    ) {
        log.info(" preparation status: starting to consult the list of preparation status");
        var preparationStatusResponseDtoList =
                preparationStatusService.getPreparationStatusList(status);
        log.info(" preparation status: finished to consult the list preparation status");
        return preparationStatusResponseDtoList;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed preparation status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed preparation status",
                    response = PreparationStatusResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public PreparationStatusResponseDTO getPreparationStatus(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("getPreparationStatus: starting to consult preparation status by id={}", id);
        var preparationStatusResponseDto =
                preparationStatusService.getPreparationStatus(id);
        log.info("getPreparationStatus: finished to consult preparation status by id={}", id);
        return preparationStatusResponseDto;
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new preparation status")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created preparation status",
                    response = PreparationStatusResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public PreparationStatusResponseDTO createPreparationStatus(
            @Valid @RequestBody PreparationStatusRequestDTO establishmentCategoryRequestDto) {
        log.info("createPreparationStatus: starting to create new preparation status");
        var preparationStatusResponseDto =
                preparationStatusService.createPreparationStatus(establishmentCategoryRequestDto);
        log.info("createPreparationStatus: finished to create new preparation status");
        return preparationStatusResponseDto;
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("/{id}")
    @ApiOperation(value = "Update preparation status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated preparation status"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updatePreparationStatus(@Valid @NotBlank @PathVariable("id") Long id,
                                        @Valid @RequestBody PreparationStatusRequestDTO preparationStatusRequestDTO) {
        log.info("updatePreparationStatus: starting update preparation status id={}", id);
        preparationStatusService.updatePreparationStatus(id, preparationStatusRequestDTO);
        log.info("updatePreparationStatus: finished update preparation status id={}", id);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete preparation status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted preparation status"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deletePreparationStatus(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("deletePreparationStatus: starting delete preparation status id={}", id);
        preparationStatusService.deletePreparationStatus(id);
        log.info("deletePreparationStatus: finished delete preparation status id={}", id);
    }
}