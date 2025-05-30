package com.infinia.sports.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración para OpenAPI (Swagger)
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la documentación OpenAPI
     * @return configuración de OpenAPI
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desarrollo");

        Contact contact = new Contact();
        contact.setName("Infinia Sports");
        contact.setEmail("info@infinia-sports.com");
        contact.setUrl("https://www.infinia-sports.com");

        License license = new License()
                .name("Licencia de Infinia Sports")
                .url("https://www.infinia-sports.com/license");

        Info info = new Info()
                .title("API de Infinia Sports")
                .version("1.0")
                .description("API para el e-commerce de productos deportivos")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
