package io.github.tonghoangvu.springsimpleshop.backofficeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBooleanProperty(name = "springdoc.api-docs.enabled", matchIfMissing = true)
@OpenAPIDefinition(
    info = @Info(title = "${spring.application.name:}", version = "${spring.application.version:}"))
public class SpringdocConfiguration {}
