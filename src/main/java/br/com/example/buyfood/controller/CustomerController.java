package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.CustomerRequestDto;
import br.com.example.buyfood.model.dto.response.CustomerResponseDto;
import br.com.example.buyfood.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @ApiOperation(value = "Returns a list of customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of customers",
                    response = CustomerResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<CustomerResponseDto> getCustomerList(@RequestParam(required = false) Integer status) {
        log.info("getCustomerlist: starting to consult the list of customers");
        var customerResponseDtoList = customerService.getCustomerList(status);
        log.info("getCustomerlist: finished to consult the list of customers");
        return customerResponseDtoList;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the informed customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed customer", response = CustomerResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public CustomerResponseDto getCustomer(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("getCustomer: starting to consult customer by id={}", id);
        var customerResponseDto = customerService.getCustomer(id);
        log.info("getCustomer: finished to consult customer by id={}", id);
        return customerResponseDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created customer", response = CustomerResponseDto.class),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public CustomerResponseDto createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        log.info("createCustomer: starting to create new customer");
        var customerResponseDto = customerService.createCustomer(customerRequestDto);
        log.info("createCustomer: finished to create new customer");
        return customerResponseDto;
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated customer"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateCustomer(@Valid @NotBlank @PathVariable("id") Long id,
                               @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        log.info("updateCustomer: starting update customer id={}", id);
        customerService.updateCustomer(id, customerRequestDto);
        log.info("updateCustomer: finished update customer id={}", id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted customer"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteCustomer(@Valid @NotBlank @PathVariable("id") Long id) {
        log.info("deleteCustomer: starting delete customer id={}", id);
        customerService.deleteCustomer(id);
        log.info("deleteCustomer: finished delete customer id={}", id);
    }
}