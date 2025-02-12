package org.hrmplatform.hrmplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
	
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				// API bilgileri
				.info(new Info()
						      .title("Human Resources Management Platform API")
						      .version("1.0")
						      .description("Human Resources Management Platform")
						      .license(new License().name("Apache 2.0").url("https://springdoc.org")))
				
				// Dış dökümantasyon
				.externalDocs(new ExternalDocumentation()
						              .description("HRMPlatform API Docs")
						              .url("https://hrmplatform-api-docs.com"))
				
				// Güvenlik özellikleri
				.components(new Components()
						            .addSecuritySchemes("bearer-token",
						                                new SecurityScheme()
								                                .type(SecurityScheme.Type.HTTP)
								                                .scheme("bearer")
								                                .bearerFormat("JWT")
						            ))
				// Güvenlik gereksinimi
				.addSecurityItem(new SecurityRequirement().addList("bearer-token"));
	}
}