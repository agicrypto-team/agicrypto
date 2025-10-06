package com.devsdoagi.agicripto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgicriptoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgicriptoApplication.class, args);
	}

}
