package com.hualala.cache.monitor;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.hualala.cache.config.Constants;

/**
 * AOP服务监听
 * @author lin.cheng
 *
 */
@Aspect
@Component
public class ServiceMonitor {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@AfterReturning(pointcut = "execution(* com.hualala..*Service.save(..)) || execution(* com.hualala..*Service.delete(..))|| execution(* com.hualala..*Service.update(..)) ", returning = "returnValue")
	public void logServiceAccess(JoinPoint joinPoint, Object returnValue) {
		String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
		if (declaringTypeName.contains("UserService")) {
			System.out.println("@AfterReturning：模拟日志记录功能...");
			System.out.println("@AfterReturning：目标方法为：" + joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName());
			System.out.println("@AfterReturning：参数为：" + Arrays.toString(joinPoint.getArgs()));
			System.out.println("@AfterReturning：返回值为：" + returnValue);
			System.out.println("@AfterReturning：被织入的目标对象为：" + joinPoint.getTarget());
			stringRedisTemplate.convertAndSend(Constants.LIST_TOPIC, "user");
		}

	}
}
