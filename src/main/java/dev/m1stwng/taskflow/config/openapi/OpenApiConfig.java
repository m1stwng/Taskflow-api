package dev.m1stwng.taskflow.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Taskflow API",
                summary = "Task management API",
                description = "An API for task management to help individuals and teams to organize, track and collaborate",
                contact = @Contact(name = "Brenno 'm1stwng'", email = "m1stwng@gmail.com"),
                license = @License(name = "MIT License", identifier = "MIT"),
                version = "0.2.0"
        ),
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication",
        bearerFormat = "JWT",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP
)
public class OpenApiConfig {
}
