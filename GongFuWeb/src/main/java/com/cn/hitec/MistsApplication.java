package com.cn.hitec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cn.hitec.tools.AppPathTool;
/**
 * 
 * @ClassName: MistsApplication 
 * @Description: TODO 系统启动入口
 * @author james
 * @date 2017年4月26日 下午6:04:44 
 *
 */
@EnableFeignClients
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MistsApplication {
    private static final Logger logger = LoggerFactory.getLogger(MistsApplication.class);
    
    @Bean
    feign.Logger.Level feignLoggerLevel(){
    	return feign.Logger.Level.FULL;
    	
    }
    public static void main(String[] args) {
        logger.info("系统启动中......");
        
        String srcPath = AppPathTool.getSrcPath();
        String dataPath = AppPathTool.getDataPath();
        logger.info("system src path is :"+srcPath);
        logger.info("system data path is :"+dataPath);
        SpringApplication.run(MistsApplication.class, args);
    }
}
