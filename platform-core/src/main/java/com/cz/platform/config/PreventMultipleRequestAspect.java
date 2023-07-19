package com.cz.platform.config;

import java.lang.reflect.Method;

import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cz.platform.exception.LoggerType;
import com.cz.platform.security.SecurityUtils;
import com.cz.platform.security.UserDTO;
import com.cz.platform.utility.PlatformCommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class PreventMultipleRequestAspect {

	@Autowired
	private PlatformCommonService platformCommonService;

	@Around("@annotation(PreventMultipleRequest)")
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		PreventMultipleRequest annotations = method.getAnnotation(PreventMultipleRequest.class);
		StringBuilder sb = new StringBuilder();
		sb.append("PREVENT_MULTIPLE_REQUEST_").append(signature.getDeclaringTypeName()).append(method.getName());
		if (BooleanUtils.isTrue(annotations.isUserSpecific())) {
			UserDTO user = SecurityUtils.getLoggedInUser();
			sb.append("_").append(user.getUserId());
		}
		RLock lock = platformCommonService.takeLock(sb.toString(), annotations.ttlInSeconds(),
				"Request being processed please wait...", LoggerType.DO_NOT_LOG);
		try {
			Object result = joinPoint.proceed();
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			platformCommonService.forceUnlock(lock);
		}
	}

}