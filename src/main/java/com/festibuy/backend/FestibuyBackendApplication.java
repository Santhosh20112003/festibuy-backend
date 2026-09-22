package com.festibuy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.festibuy")
public class FestibuyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FestibuyBackendApplication.class, args);
	}

}

