package com.eticare.eticaretAPI;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Eticaret App",version = "1.0",description = "Eticaret  Projesi"))
public class EticaretApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EticaretApiApplication.class, args);
	}

}
