package io.github.innobridge.mcpserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Model Context Protocol Server")
                        .description("API for Model Context Protocol Server")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("InnoBridge")
                                .url("https://github.com/innobridge")
                                .email("info@innobridge.com"))
                        .license(new License()
                                .name("InnoBridge License")
                                .url("https://github.com/innobridge")));
    }

    // If you need to configure resource handlers for Swagger UI, you can create a WebFluxConfigurer bean
    @Bean
    public WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
            }
        };
    }
}
