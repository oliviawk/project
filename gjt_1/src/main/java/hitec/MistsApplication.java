package hitec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @ClassName: MistsApplication 
 * @Description: TODO 系统启动入口
 * @author james
 * @date 2018/1/17
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
        
        SpringApplication.run(MistsApplication.class, args);
    }
}
