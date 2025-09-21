package com.vnsky.bcss.projectbase.wire.springdoc.infrastructure.primary;

import com.vnsky.bcss.projectbase.shared.generation.domain.ExcludeFromGeneratedCodeCoverage;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ExcludeFromGeneratedCodeCoverage(reason = "Not testing technical configuration")
class SpringdocConfiguration {

    @Value("${application.version:undefined}")
    private String version;

    @Bean
    public OpenAPI catalogOpenAPI() {
        return new OpenAPI()
            .info(swaggerInfo())
            .externalDocs(swaggerExternalDoc())
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components().addSecuritySchemes
                ("Bearer Authentication", createAPIKeyScheme()));
    }

    private Info swaggerInfo() {
        return new Info()
            .title("Customer Service API")
            .description("Customer Service API")
            .version(version)
            .license(new License().name("No license"));
    }

    private ExternalDocumentation swaggerExternalDoc() {
        return new ExternalDocumentation().description("Customer Service API Documentation");
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer");
    }
}
