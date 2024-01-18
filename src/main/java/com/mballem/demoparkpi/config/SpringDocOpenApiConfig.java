package com.mballem.demoparkpi.config;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("security", securityScheme()))
                .info(
                        new Info()
                                .title("REST API - Spring Park")
                                .description("API para gestão de estacionamento de veículos")
                                .version("v1")
                                .license(new License().name("Apache 3.0").url("https://www.apache.org/licenses/LICENSE-3.0"))
                                .contact(new Contact().name("Marcio Ballem").email("marcio@spring-park.com"))
                );
    }

    private SecurityScheme securityScheme(){
        return new SecurityScheme().description("Inserir um Bearer token para prosseguir")
                .type(SecurityScheme.Type.HTTP).in(SecurityScheme.In.HEADER).scheme("bearer")
                .bearerFormat("JWT").name("security");
    }

}
