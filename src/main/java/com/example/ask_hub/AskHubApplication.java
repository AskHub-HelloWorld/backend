package com.example.ask_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AskHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AskHubApplication.class, args);
	}

}
