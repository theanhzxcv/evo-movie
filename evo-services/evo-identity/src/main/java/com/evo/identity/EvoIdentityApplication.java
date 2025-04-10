package com.evo.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class EvoIdentityApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvoIdentityApplication.class, args);
	}

}
