package es.courselab.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("CourseLab - REST API")
                        .description("Documentación de la aplicación CourseLab Running")
                        .contact(new Contact()
                                .name("CourseLab")
                                .email("info@courselab.com")
                                .url("www.courselab.com"))
                        .version("1.0"));
    }
}
