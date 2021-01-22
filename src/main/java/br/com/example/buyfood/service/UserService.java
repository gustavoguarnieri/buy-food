package br.com.example.buyfood.service;

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
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
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

    private final String role = "USER";
    private final String user = "admin";
    private final String pass = "Pa55w0rd";

    public UserCreateRequestDto createUser(UserCreateRequestDto userCreateRequestDTO){

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

        try {
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

            } else {

                log.warn("Unexpected status code status={}", response.getStatus() + "-" +
                        response.getStatusInfo().toString());
            }

        } catch (Exception e){

            log.error("An error occurred when creating the user={}", user.toString());
        }

        return userCreateRequestDTO;
    }

    public AccessTokenResponse signin(UserSigninRequestDto userSignin){

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", CredentialRepresentation.PASSWORD);

        AccessTokenResponse response = null;
        try{
            Configuration configuration = new Configuration(authServerUrl, realm, clientId,
                    clientCredentials, null);
            AuthzClient authzClient = AuthzClient.create(configuration);
            response = authzClient.obtainAccessToken(userSignin.getEmail(), userSignin.getPassword());
        } catch (Exception e) {
            log.error("An error occurred when signing user={}", userSignin.getEmail());
        }

        return response;
    }
}
