package com.crediya.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;
    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Crediya Reporting Service API")
                        .description("API para el servicio de reporting de Crediya")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Crediya Development Team")
                                .email("dev@crediya.com"))
                        .license(new License()
                                .name("Private License")
                                .url("https://crediya.com/license")))
                .components(new Components().addSecuritySchemes(
                        schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList(schemeName)) // <-- 🔑 obliga a pasar el token
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Desarrollo Local"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Docker Container")
                ));
    }


    @Bean
    public GroupedOpenApi reportingApiGroup() {
        return GroupedOpenApi.builder()
                .group("reporting")
                .displayName("Crediya Reporting API")
                .pathsToMatch("/api/usecase/path")
                .build();
    }

}
