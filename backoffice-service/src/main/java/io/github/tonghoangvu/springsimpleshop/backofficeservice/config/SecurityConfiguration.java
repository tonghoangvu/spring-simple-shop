package io.github.tonghoangvu.springsimpleshop.backofficeservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  private static final String[] UNAUTHENTICATED_PATHS = {
    "/actuator/**",
    "/static/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs",
    "/v3/api-docs.yaml",
    "/v3/api-docs/swagger-config",
    "/webjars/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity httpSecurity, HandlerExceptionResolver handlerExceptionResolver) {
    httpSecurity.authorizeHttpRequests(
        configurer ->
            configurer
                .requestMatchers(UNAUTHENTICATED_PATHS)
                .permitAll()
                .anyRequest()
                .authenticated());
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.exceptionHandling(
        configurer ->
            configurer
                .authenticationEntryPoint(
                    new ApplicationAuthenticationEntryPoint(handlerExceptionResolver))
                .accessDeniedHandler(new ApplicationAccessDeniedHandler(handlerExceptionResolver)));
    httpSecurity.sessionManagement(
        configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return httpSecurity.build();
  }

  @RequiredArgsConstructor
  public static class ApplicationAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) {
      handlerExceptionResolver.resolveException(request, response, null, authException);
    }
  }

  @RequiredArgsConstructor
  public static class ApplicationAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) {
      handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
  }
}
