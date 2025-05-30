package com.infinia.sports.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para la aplicación
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura controladores de vista para redireccionar la raíz a Swagger UI
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }
}
