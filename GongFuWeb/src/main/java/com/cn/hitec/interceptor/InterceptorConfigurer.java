package com.cn.hitec.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName: InterceptorConfigurer
 * @Description: TODO(拦截器配置类)
 * @author ZhangLu
 * @date 2017年6月6日 下午4:25:47
 * 
 */
@Configuration 
public class InterceptorConfigurer extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链

		// addPathPatterns 用于添加拦截规则

		// excludePathPatterns 用户排除拦截

		registry.addInterceptor(new SystemInterceptor()).addPathPatterns("/**");

		super.addInterceptors(registry);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);

	}
}
