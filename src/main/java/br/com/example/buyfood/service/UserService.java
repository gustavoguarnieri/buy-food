package br.com.example.buyfood.service;

import br.com.example.buyfood.model.dto.request.UserCreateRequestDto;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDto;
import br.com.example.buyfood.model.dto.response.UserCreateResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public UserCreateResponseDto createUser(UserCreateRequestDto userCreateRequestDto){

        String ROLE = "user";
        String USER = "admin";
        String PASS = "Pa55w0rd";

        var keycloak = getKeycloakBuilder(USER, PASS);

        var user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userCreateRequestDto.getEmail());
        user.setFirstName(userCreateRequestDto.getFirstname());
        user.setLastName(userCreateRequestDto.getLastname());
        user.setEmail(userCreateRequestDto.getEmail());

        var realmResource = keycloak.realm(realm);
        var usersResource = realmResource.users();

        var userCreateResponseDto = new UserCreateResponseDto();

        try {
            var response = usersResource.create(user);

            userCreateResponseDto.setStatusCode(response.getStatus());
            userCreateResponseDto.setStatus(response.getStatusInfo().toString());

            if (response.getStatus() == HttpStatus.CREATED.value()) {

                String userId = CreatedResponseUtil.getCreatedId(response);

                log.info("Created userId {}", userId);

                var passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(userCreateRequestDto.getPassword());

                UserResource userResource = usersResource.get(userId);
                userResource.resetPassword(passwordCred);

                RoleRepresentation realmRoleUser = realmResource.roles().get(ROLE).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(realmRoleUser));

            } else {

                log.warn("Unexpected status code status={}", response.getStatus() + "-" +
                        response.getStatusInfo().toString());
            }

        } catch (Exception e){

            log.error("An error occurred when creating the user={}", user.toString());
        }

        return userCreateResponseDto;
    }

    private Keycloak getKeycloakBuilder(String USER, String PASS) {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(USER)
                .password(PASS)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
    }

    public AccessTokenResponse signin(UserSigninRequestDto userSignin){

        Map<String, Object> clientCredentials = getClientCredentials();

        AccessTokenResponse response = null;
        try{
            var configuration = new Configuration(authServerUrl, realm, clientId,
                    clientCredentials, null);
            var authzClient = AuthzClient.create(configuration);
            response = authzClient.obtainAccessToken(userSignin.getEmail(), userSignin.getPassword());
        } catch (Exception e) {
            log.error("An error occurred when signing user={}", userSignin.getEmail());
        }

        return response;
    }

    private Map<String, Object> getClientCredentials() {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", CredentialRepresentation.PASSWORD);
        return clientCredentials;
    }

    public String getUserId(){
        return getKeycloakClaims().get("user_id").toString();
    }

    private Map<String, Object> getKeycloakClaims() {
        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        var principal = (Principal) authentication.getPrincipal();

        var keycloakPrincipal = (KeycloakPrincipal) principal;
        var token = keycloakPrincipal.getKeycloakSecurityContext().getToken();
        Map<String, Object> customClaims = token.getOtherClaims();
        return customClaims;
    }
}