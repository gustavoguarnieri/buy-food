package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.DeliveryAddressRequestDTO;
import br.com.example.buyfood.model.dto.response.DeliveryAddressResponseDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.service.AddressService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/users")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/addresses")
    @ApiOperation(value = "Returns a list of user address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of user address",
                    response = ProductResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<DeliveryAddressResponseDTO> getUserAddressList(@RequestParam(required = false) Integer status) {
        log.info("getUserAddressList: starting to consult the list of user address");
        var addressResponseDto = addressService.getUserAddressList(status);
        log.info("getUserAddressList: finished to consult the list of user address");
        return addressResponseDto;
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation(value = "Returns the informed user address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed user address",
                    response = ProductResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryAddressResponseDTO getUserAddress(@Valid @NotBlank @PathVariable("addressId") Long addressId) {
        log.info("getUserAddress: starting to consult user address by addressId={}", addressId);
        var addressResponseDto = addressService.getUserAddress(addressId);
        log.info("getUserAddress: finished to consult user address by addressId={}", addressId);
        return addressResponseDto;
    }

    @PostMapping("/address")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new user address")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created user address", response = ImageResponseDTO.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public DeliveryAddressResponseDTO createUserAddress(@Valid @RequestBody DeliveryAddressRequestDTO deliveryAddressRequestDto) {
        log.info("createUserAddress: starting to create new user address");
        var addressResponseDto = addressService
                .createUserAddress(deliveryAddressRequestDto);
        log.info("createUserAddress: finished to create new user address");
        return addressResponseDto;
    }

    @PutMapping("/address/{addressId}")
    @ApiOperation(value = "Update user address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated user address"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateUserAddress(@Valid @NotBlank @PathVariable("addressId") Long addressId,
                                  @Valid @RequestBody DeliveryAddressRequestDTO deliveryAddressRequestDto) {
        log.info("updateUserAddress: starting update user address by addressId={}", addressId);
        addressService.updateUserAddress(addressId, deliveryAddressRequestDto);
        log.info("updateUserAddress: finished update user address by addressId={}", addressId);
    }

    @DeleteMapping("/address/{addressId}")
    @ApiOperation(value = "Delete user address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted user address"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteUserAddress(@Valid @NotBlank @PathVariable("addressId") Long addressId) {
        log.info("deleteUserAddress: starting delete user address addressId={}", addressId);
        addressService.deleteUserAddress(addressId);
        log.info("deleteUserAddress: finished delete user address addressId={}", addressId);
    }
}