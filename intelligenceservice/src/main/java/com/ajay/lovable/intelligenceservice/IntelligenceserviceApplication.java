package com.ajay.lovable.intelligenceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IntelligenceserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelligenceserviceApplication.class, args);
	}

}
