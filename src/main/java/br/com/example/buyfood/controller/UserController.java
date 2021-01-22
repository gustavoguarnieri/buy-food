package br.com.example.buyfood.controller;

import br.com.example.buyfood.model.dto.request.UserCreateRequestDto;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping(value = "/users")
@RestController
public class UserController {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private String role = "USER";
    private String user = "admin";
    private String pass = "Pa55w0rd";

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequestDto userCreateRequestDTO) {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(user)
                .password(pass)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userCreateRequestDTO.getEmail());
        user.setFirstName(userCreateRequestDTO.getFirstname());
        user.setLastName(userCreateRequestDTO.getLastname());
        user.setEmail(userCreateRequestDTO.getEmail());

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(user);

        userCreateRequestDTO.setStatusCode(response.getStatus());
        userCreateRequestDTO.setStatus(response.getStatusInfo().toString());

        if (response.getStatus() == 201) {

            String userId = CreatedResponseUtil.getCreatedId(response);

            log.info("Created userId {}", userId);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userCreateRequestDTO.getPassword());

            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(passwordCred);

            RoleRepresentation realmRoleUser = realmResource.roles().get(role).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(realmRoleUser));
        }
        return ResponseEntity.ok(userCreateRequestDTO);
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<?> signin(@RequestBody UserSigninRequestDto userSignin) {

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", CredentialRepresentation.PASSWORD);

        Configuration configuration =
                new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response =
                authzClient.obtainAccessToken(userSignin.getEmail(), userSignin.getPassword());

        return ResponseEntity.ok(response);
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

