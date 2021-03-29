package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.response.Product.EstablishmentProductResponseDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.service.ProductUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/v1/users/products")
public class ProductUserController {

    @Autowired
    private ProductUserService productUserService;

    @GetMapping
    @ApiOperation(value = "Returns a list of product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a list of product",
                    response = ProductResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public List<EstablishmentProductResponseDTO> getProductList(
            @RequestParam(required = false) Integer status) {
        log.info("getProductList: starting to consult the list of product");
        var productResponseDtoList = productUserService.getProductList(status);
        log.info("getProductList: finished to consult the list of product");
        return productResponseDtoList;
    }
}