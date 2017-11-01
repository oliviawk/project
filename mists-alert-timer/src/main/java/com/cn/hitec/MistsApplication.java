package com.cn.hitec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MistsApplication {
	@Bean
	feign.Logger.Level feignLoggerLevel(){
		return feign.Logger.Level.FULL;

	}
	public static void main(String[] args) {
		SpringApplication.run(MistsApplication.class, args);
	}
}
