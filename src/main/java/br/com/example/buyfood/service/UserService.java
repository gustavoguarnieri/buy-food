package br.com.example.buyfood.service;

import br.com.example.buyfood.exception.BusinessException;
import br.com.example.buyfood.exception.ConflitException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.UserCreateRequestDTO;
import br.com.example.buyfood.model.dto.request.UserSigninRequestDTO;
import br.com.example.buyfood.model.dto.request.UserUpdateRequestDTO;
import br.com.example.buyfood.model.dto.response.UserCreateResponseDTO;
import br.com.example.buyfood.model.embeddable.Audit;
import br.com.example.buyfood.model.entity.UserEntity;
import br.com.example.buyfood.model.repository.UserRepository;
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
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Value("${keycloak-custom.admin-user}")
    private String adminUser;

    @Value("${keycloak-custom.admin-password}")
    private String adminPass;

    @Autowired
    private UserRepository userRepository;

    public UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDto) {

        var userEntity = saveCustomUser(userCreateRequestDto);

        var ROLE = userCreateRequestDto.getRole().name();

        var keycloak = getKeycloakBuilder(adminUser, adminPass);

        var user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userCreateRequestDto.getEmail());
        user.setFirstName(userCreateRequestDto.getFirstName());
        user.setLastName(userCreateRequestDto.getLastName());
        user.setEmail(userCreateRequestDto.getEmail());

        var realmResource = keycloak.realm(realm);
        var usersResource = realmResource.users();

        var userCreateResponseDto = new UserCreateResponseDTO();

        try {
            var response = usersResource.create(user);

            userCreateResponseDto.setStatusCode(response.getStatus());
            userCreateResponseDto.setStatus(response.getStatusInfo().toString());

            if (response.getStatus() == HttpStatus.CREATED.value()) {
                var userId = CreatedResponseUtil.getCreatedId(response);
                userCreateResponseDto.setUserId(userId);

                log.info("createUser: Created user userId={} userMail={}", userId, userCreateRequestDto.getEmail());

                var passwordCred = getCredentialRepresentation(userCreateRequestDto.getPassword());

                UserResource userResource = usersResource.get(userId);
                userResource.resetPassword(passwordCred);

                var realmRoleUser = realmResource.roles().get(ROLE).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(realmRoleUser));

                userEntity.setUserId(userId);
                userEntity.getAudit().setCreatedBy(userId);
                userRepository.save(userEntity);

            } else {
                deleteCustomUser(userEntity.getUserId());
                log.warn("createUser: Unexpected status code for userMail= {} status={}",
                        user.getEmail(), response.getStatus() + "-" + response.getStatusInfo().toString());
            }

        } catch (Exception ex) {
            log.error("createUser: An error occurred when creating the user={} ", user.getUsername(), ex);
        }

        return userCreateResponseDto;
    }

    private CredentialRepresentation getCredentialRepresentation(String password) {
        var passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        return passwordCred;
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
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(15).build()).build();
    }

    public AccessTokenResponse signin(UserSigninRequestDTO userSignin) {
        var clientCredentials = getClientCredentials();

        AccessTokenResponse response = null;
        try {
            var configuration = new Configuration(authServerUrl, realm, clientId,
                    clientCredentials, null);
            var authzClient = AuthzClient.create(configuration);
            response = authzClient.obtainAccessToken(userSignin.getEmail(), userSignin.getPassword());
        } catch (Exception ex) {
            log.error("signin: An error occurred when signing user={}", userSignin.getEmail(), ex);
        }
        return response;
    }

    private Map<String, Object> getClientCredentials() {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", CredentialRepresentation.PASSWORD);
        return clientCredentials;
    }

    public Optional<String> getUserId() {
        try {
            var keycloakClaims = getKeycloakClaims();
            return keycloakClaims == null ? Optional.empty() : Optional.ofNullable(keycloakClaims.get("user_id").toString());
        } catch (Exception ex) {
            log.error("getUserId: An error occurred when getUserId, user ", ex);
            return Optional.empty();
        }
    }

    private Map<String, Object> getKeycloakClaims() {

        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            return null;
        }

        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        var principal = (Principal) authentication.getPrincipal();

        var keycloakPrincipal = (KeycloakPrincipal) principal;
        var token = keycloakPrincipal.getKeycloakSecurityContext().getToken();
        return token.getOtherClaims();
    }

    private UserEntity saveCustomUser(UserCreateRequestDTO userCreateRequestDto) {

        var user = userRepository.findByEmail(userCreateRequestDto.getEmail());

        if (user.isPresent()) {
            log.warn("saveCustomUser: Duplicated resource, this email={} already exist ",
                    userCreateRequestDto.getEmail());
            throw new ConflitException("Duplicated resource, this email already exist");
        }

        try {
            return userRepository.save(
                    new UserEntity(
                            null,
                            userCreateRequestDto.getFirstName(),
                            userCreateRequestDto.getLastName(),
                            userCreateRequestDto.getNickName(),
                            userCreateRequestDto.getEmail(),
                            userCreateRequestDto.getPhone(),
                            userCreateRequestDto.getBirthDate(),
                            userCreateRequestDto.getCpf(),
                            userCreateRequestDto.getCnpj(),
                            new Audit(null)
                    )
            );
        } catch (Exception ex) {
            log.error("saveCustomUser: An error occurred when save user={} ", userCreateRequestDto.getEmail(), ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public void updateCustomUser(String userId, UserUpdateRequestDTO userUpdateRequestDto) {
        var userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        try {
            userEntity.setFirstName(userUpdateRequestDto.getFirstName());
            userEntity.setLastName(userUpdateRequestDto.getLastName());
            userEntity.setNickName(userUpdateRequestDto.getNickName());
            userEntity.setPhone(userUpdateRequestDto.getPhone());
            userEntity.getAudit().setLastUpdatedBy(userId);
            userRepository.save(userEntity);
        } catch (Exception ex) {
            log.error("deleteCustomUser: An error occurred when update userId={} ", userId, ex);
            throw new BusinessException(ex.getMessage());
        }

        try {
            var keycloak = getKeycloakBuilder(adminUser, adminPass);
            var realmResource = keycloak.realm(realm);
            var usersResource = realmResource.users();

            var user = usersResource.get(userEntity.getUserId()).toRepresentation();
            user.setFirstName(userUpdateRequestDto.getFirstName());
            user.setLastName(userUpdateRequestDto.getLastName());

            var passwordCred = getCredentialRepresentation(userUpdateRequestDto.getPassword());
            var userResource = usersResource.get(userId);
            userResource.resetPassword(passwordCred);

            usersResource.get(userEntity.getUserId()).update(user);
        } catch (Exception ex) {
            log.error("updateCustomUser: An error occurred when update keycloak user={} ", userEntity.getEmail(), ex);
            throw new BusinessException(ex.getMessage());
        }

    }

    public void deleteCustomUser(String userId) {
        var userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        try {
            userEntity.setStatus(0);
            userRepository.save(userEntity);
        } catch (Exception ex) {
            log.error("deleteCustomUser: An error occurred when update userId={} ", userId, ex);
            throw new BusinessException(ex.getMessage());
        }

        try {
            var keycloak = getKeycloakBuilder(adminUser, adminPass);
            var realmResource = keycloak.realm(realm);
            var usersResource = realmResource.users();

            var user = usersResource.get(userEntity.getUserId()).toRepresentation();
            user.setEnabled(false);
            usersResource.get(userEntity.getUserId()).update(user);
        } catch (Exception ex) {
            log.error("deleteCustomUser: An error occurred when update keycloak user={} ", userEntity.getEmail(), ex);
            throw new BusinessException(ex.getMessage());
        }
    }
}