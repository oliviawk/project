package com.cn.hitec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling	//开启定时任务配置
public class MistsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MistsApplication.class, args);
	}
}
