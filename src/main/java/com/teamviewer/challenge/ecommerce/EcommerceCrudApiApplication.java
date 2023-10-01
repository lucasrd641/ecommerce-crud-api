package com.teamviewer.challenge.ecommerce;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Ecommerce CRUD API", version = "1.0", description = "Ecommerce CRUD Operations"))
public class EcommerceCrudApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(EcommerceCrudApiApplication.class, args);
	}
}
