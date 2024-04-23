package com.pweb.springBackend.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Web Programming - Spring Boot Backend API");

        Contact myContact = new Contact();
        myContact.setName("Mihnea Blotiu");
        myContact.setEmail("mblotiu.ss@gmail.com");

        Info information = new Info()
                .title("Todo Lists System API")
                .version("1.0")
                .description("This API exposes endpoints to manage todos / users / etc.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server)).addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme().name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
