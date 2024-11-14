package com.testdemo.testdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI springOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("CRUD Application API")
						.description("Spring Boot CRUD API Documentation")
						.version("1.0")
						.contact(new Contact()
								.name("Your Name")
								.email("your.email@example.com")));
	}
}
