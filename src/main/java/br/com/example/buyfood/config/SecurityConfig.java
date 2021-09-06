package br.com.example.buyfood.config;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  private final SwaggerConfig swaggerConfig;
  private static final String AUTHORITY_MAPPER_PREFIX = "ROLE_";
  private static final String PROFILE_ADMIN = "ADMIN";
  private static final String PROFILE_ESTABLISHMENT = "ESTABLISHMENT";
  private static final String PROFILE_USER = "USER";

  public SecurityConfig(SwaggerConfig swaggerConfig) {
    this.swaggerConfig = swaggerConfig;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    super.configure(http);

    http.cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(swaggerConfig.swaggerAuthWhiteList())
        .permitAll()
        .antMatchers("/index.html")
        .permitAll()
        .antMatchers("/actuator/health")
        .permitAll()
        .antMatchers("/api/v1/users/create")
        .permitAll()
        .antMatchers("/api/v1/users/signin")
        .permitAll()
        .antMatchers(
            "/api/v1/establishments/{establishmentId}/products/{productId}/images/download-file/**")
        .permitAll()
        .antMatchers("/api/v1/establishments/{establishmentId}/images/download-file/**")
        .permitAll()
        .antMatchers("/api/v1/admin/**")
        .hasRole(PROFILE_ADMIN)
        .antMatchers("/api/v1/users/**")
        .hasAnyRole(PROFILE_USER, PROFILE_ESTABLISHMENT, PROFILE_ADMIN)
        .antMatchers("/api/v1/establishments/**")
        .hasAnyRole(PROFILE_USER, PROFILE_ESTABLISHMENT, PROFILE_ADMIN)
        .anyRequest()
        .authenticated();
  }

  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {

    /** Returning NullAuthenticatedSessionStrategy means app will not remember session */
    return new NullAuthenticatedSessionStrategy();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {

    SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
    grantedAuthorityMapper.setPrefix(AUTHORITY_MAPPER_PREFIX);
    grantedAuthorityMapper.setConvertToUpperCase(true);

    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthorityMapper);
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public FilterRegistrationBean<KeycloakAuthenticationProcessingFilter>
      keycloakAuthenticationProcessingFilterRegistrationBean(
          KeycloakAuthenticationProcessingFilter filter) {

    FilterRegistrationBean<KeycloakAuthenticationProcessingFilter> registrationBean =
        new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<KeycloakPreAuthActionsFilter>
      keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {

    FilterRegistrationBean<KeycloakPreAuthActionsFilter> registrationBean =
        new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<KeycloakAuthenticatedActionsFilter>
      keycloakAuthenticatedActionsFilterBean(KeycloakAuthenticatedActionsFilter filter) {

    FilterRegistrationBean<KeycloakAuthenticatedActionsFilter> registrationBean =
        new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<KeycloakSecurityContextRequestFilter>
      keycloakSecurityContextRequestFilterBean(KeycloakSecurityContextRequestFilter filter) {

    FilterRegistrationBean<KeycloakSecurityContextRequestFilter> registrationBean =
        new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  @Override
  @ConditionalOnMissingBean(HttpSessionManager.class)
  protected HttpSessionManager httpSessionManager() {
    return new HttpSessionManager();
  }
}
