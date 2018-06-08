package com.cn.hitec.interceptor;

import org.apache.lucene.document.DateTools;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: SystemInterceptor
 * @Description: TODO(系统拦截器)
 * @author ZhangLu
 * @date 2017年6月6日 下午4:25:35
 * 
 */
public class SystemInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// System.out.println(">>>系统拦截器>>>>>>>在整个请求结束之后被调用，也就是在DispatcherServlet
		// 渲染了对应的视图之后执行（主要是用于进行资源清理工作）");
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// System.out.println(">>>系统拦截器>>>>>>>请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）");
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
//		 System.out.println(">>>系统拦截器>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");
//		 System.out.println(">>>请求的IP地址:"+arg0.getRemoteAddr());
		return true;// 只有返回true才会继续向下执行，返回false取消当前请求
	}

}
