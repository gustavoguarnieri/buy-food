package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.EstablishmentRequestDTO;
import br.com.example.buyfood.model.dto.response.EstablishmentResponseDTO;
import br.com.example.buyfood.service.EstablishmentService;
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
public class EstablishmentController {

    @Autowired
    private EstablishmentService establishmentService;

    @GetMapping
    @ApiOperation(value = "Returns a list of establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of establishment",
                    response = EstablishmentResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<EstablishmentResponseDTO> getEstablishmentList(@RequestParam(required = false) Integer status) {
        log.info("getEstablishmentList: starting to consult the list of establishment");
        var establishmentResponseDtoList = establishmentService.getEstablishmentList(status);
        log.info("getEstablishmentList: finished to consult the list of establishment");
        return establishmentResponseDtoList;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed establishment",
                    response = EstablishmentResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public EstablishmentResponseDTO getEstablishment(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("getEstablishment: starting to consult establishment by id={}", id);
        var establishmentResponseDto = establishmentService.getEstablishment(id);
        log.info("getEstablishment: finished to consult establishment by id={}", id);
        return establishmentResponseDto;
    }

    @GetMapping("/mine")
    @ApiOperation(value = "Returns my establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns my establishment",
                    response = EstablishmentResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<EstablishmentResponseDTO> getMyEstablishmentList(@RequestParam(required = false) Integer status) {
        log.info("getMyEstablishmentList: starting to consult my establishment");
        var establishmentResponseDto = establishmentService.getMyEstablishmentList(status);
        log.info("getMyEstablishmentList: finished to consult my establishment");
        return establishmentResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created establishment", response = EstablishmentResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public EstablishmentResponseDTO createEstablishment(
            @Valid @RequestBody EstablishmentRequestDTO establishmentRequestDto) {
        log.info("createEstablishment: starting to create new establishment");
        var establishmentResponseDto = establishmentService
                .createEstablishment(establishmentRequestDto);
        log.info("createEstablishment: finished to create new establishment");
        return establishmentResponseDto;
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    @ApiOperation(value = "Update establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated establishment"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateEstablishment(@Valid @NotBlank @PathVariable("id") Long id,
                                    @Valid @RequestBody EstablishmentRequestDTO establishmentRequestDto) {
        log.info("updateEstablishment: starting update establishment id={}", id);
        establishmentService.updateEstablishment(id, establishmentRequestDto);
        log.info("updateEstablishment: finished update establishment id={}", id);
    }

    @Secured({"ROLE_ESTABLISHMENT", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted establishment"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteEstablishment(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("deleteEstablishment: starting delete establishment id={}", id);
        establishmentService.deleteEstablishment(id);
        log.info("deleteEstablishment: finished delete establishment id={}", id);
    }
}