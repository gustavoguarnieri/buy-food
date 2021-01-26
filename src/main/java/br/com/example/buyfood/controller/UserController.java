package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.UserCreateRequestDto;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDto;
import br.com.example.buyfood.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created user"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @PostMapping(path = "/create")
    public UserCreateRequestDto createUser(@RequestBody UserCreateRequestDto userCreateRequestDTO) {

        log.info("createUser: starting create user firstname={} email={}",
                userCreateRequestDTO.getFirstname(), userCreateRequestDTO.getEmail());

        var createUserResponse =  userService.createUser(userCreateRequestDTO);

        log.info("createUser: finishing create user firstname={} email={}",
                userCreateRequestDTO.getFirstname(), userCreateRequestDTO.getEmail());

        return createUserResponse;
    }

    @ApiOperation(value = "Signin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @PostMapping(path = "/signin")
    public AccessTokenResponse signin(@RequestBody UserSigninRequestDto userSignin) {

        log.info("signin: starting signin user email={}", userSignin.getEmail());

        var userSigninResponse = userService.signin(userSignin);

        log.info("signin: finishing signin user email={}", userSignin.getEmail());

        return userSigninResponse;
    }

    @Secured("ADMIN")
    @GetMapping(value = "/admin")
    public String getAdminData() {
        return "Hello, this endpoint is just for admin role.";
    }
}

