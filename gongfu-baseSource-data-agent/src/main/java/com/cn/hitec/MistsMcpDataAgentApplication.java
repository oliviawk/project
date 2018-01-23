package com.cn.hitec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.cn.hitec.repository.jpa")
public class MistsMcpDataAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MistsMcpDataAgentApplication.class, args);
	}
}
