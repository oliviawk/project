package com.hitec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@SpringBootApplication
@EnableJpaRepositories("com.hitec.repository.jpa")
public class ConfigProjectApplication {

	@Bean
	feign.Logger.Level feignLoggerLevel(){
		return feign.Logger.Level.FULL;

	}

	public static void main(String[] args) {
		
		SpringApplication.run(ConfigProjectApplication.class, args);
		
	}
}
