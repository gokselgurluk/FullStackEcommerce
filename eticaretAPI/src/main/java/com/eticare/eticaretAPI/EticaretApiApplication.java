package com.eticare.eticaretAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EticaretApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EticaretApiApplication.class, args);
	}

}
