package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.UserCreateRequestDto;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDto;
import br.com.example.buyfood.model.dto.request.UserUpdateRequestDto;
import br.com.example.buyfood.model.dto.response.UserCreateResponseDto;
import br.com.example.buyfood.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created user", response = UserCreateResponseDto.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    @PostMapping(path = "/create")
    public UserCreateResponseDto createUser(@RequestBody UserCreateRequestDto userCreateRequestDTO) {
        log.info("createUser: starting create user firstname={} email={}",
                userCreateRequestDTO.getFirstName(), userCreateRequestDTO.getEmail());
        var createUserResponse = userService.createUser(userCreateRequestDTO);
        log.info("createUser: finishing create user firstname={} email={}",
                userCreateRequestDTO.getFirstName(), userCreateRequestDTO.getEmail());
        return createUserResponse;
    }

    @ApiOperation(value = "Signin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AccessTokenResponse.class),
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

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{userId}")
    @ApiOperation(value = "Update user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated user"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void updateUser(@Valid @NotBlank @PathVariable("userId") String userId,
                           @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        log.info("updateUser: starting update user userId={}", userId);
        userService.updateCustomUser(userId, userUpdateRequestDto);
        log.info("updateUser: finished update user userId={}", userId);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted user"),
            @ApiResponse(code = 401, message = "You are unauthorized to access this resource"),
            @ApiResponse(code = 403, message = "You do not have permission to access this resource"),
            @ApiResponse(code = 500, message = "An exception was thrown"),
    })
    public void deleteUser(@Valid @NotBlank @PathVariable("userId") String userId) {
        log.info("deleteUser: starting delete user userId={}", userId);
        userService.deleteCustomUser(userId);
        log.info("deleteUser: finished delete user userId={}", userId);
    }
}

