package com.capstone.bowlingbling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "com.capstone.bowlingbling.domain")
@EnableJpaRepositories(basePackages = "com.capstone.bowlingbling.domain")
public class BowlingBlingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BowlingBlingApplication.class, args);
	}

}
