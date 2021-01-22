package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.UserCreateRequestDto;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDto;
import br.com.example.buyfood.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/create")
    public UserCreateRequestDto createUser(@RequestBody UserCreateRequestDto userCreateRequestDTO) {

        log.info("createUser: starting create user firstname={} email={}",
                userCreateRequestDTO.getFirstname(), userCreateRequestDTO.getEmail());

        var createUserResponse =  userService.createUser(userCreateRequestDTO);

        log.info("createUser: finishing create user firstname={} email={}",
                userCreateRequestDTO.getFirstname(), userCreateRequestDTO.getEmail());

        return createUserResponse;
    }

    @PostMapping(path = "/signin")
    public AccessTokenResponse signin(@RequestBody UserSigninRequestDto userSignin) {

        log.info("signin: starting signin user email={}", userSignin.getEmail());

        var userSigninResponse = userService.signin(userSignin);

        log.info("signin: finishing signin user email={}", userSignin.getEmail());

        return userSigninResponse;
    }

    @GetMapping(value = "/unprotected-data")
    public String getName() {
        return "Hello, this api is not protected.";
    }

    @GetMapping(value = "/protected-data")
    public String getEmail() {
        return "Hello, this api is protected.";
    }
}

