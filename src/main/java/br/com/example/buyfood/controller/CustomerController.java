package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.response.CustomerResponseDto;
import br.com.example.buyfood.model.dto.request.CustomerRequestDto;
import br.com.example.buyfood.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @ApiOperation(value = "Returns a list of customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of customers"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @GetMapping(produces="application/json")
    public List<CustomerResponseDto> getCustomerList(){

        log.info("getCustomerlist: getting list of customers");
        List<CustomerResponseDto> customerResponseDtoList = customerService.getCustomerList();
        log.info("getCustomerlist: got list of customers");

        return customerResponseDtoList;
    }

    @ApiOperation(value = "Returns the informed customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns the informed customer"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @GetMapping("/{id}")
    public CustomerResponseDto getCustomer(@Valid @NotBlank @PathVariable("id") Long id){

        log.info("getCustomer: getting customer by id={}", id);
        CustomerResponseDto customerResponseDto = customerService.getCustomer(id);
        log.info("getCustomer: got customer by id={}", id);

        return customerResponseDto;
    }

    @ApiOperation(value = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created customer"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDto createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {

        log.info("createCustomer: creating a new customer");
        CustomerResponseDto customerResponseDto = customerService.createCustomer(customerRequestDto);
        log.info("createCustomer: created the customer");

        return customerResponseDto;
    }

    @ApiOperation(value = "Update customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated customer"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @PutMapping("/{id}")
    public void updateCustomer(@Valid @NotBlank @PathVariable("id") Long id,
                                              @Valid @RequestBody CustomerRequestDto customerRequestDto) {

        log.info("updateCustomer: updating customer id={}", id);
        customerService.updateCustomer(id, customerRequestDto);
        log.info("updateCustomer: updated customer id={}", id);
    }

    @ApiOperation(value = "Delete customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted customer"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @DeleteMapping("/{id}")
    public void deleteCustomer(@Valid @NotBlank @PathVariable("id") Long id) {

        log.info("deleteCustomer: deleting customer id={}", id);
        customerService.deleteCustomer(id);
        log.info("deleteCustomer: deleted customer id={}", id);
    }
}
