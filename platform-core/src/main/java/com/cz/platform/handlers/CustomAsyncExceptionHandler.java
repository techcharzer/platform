package com.cz.platform.handlers;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		log.error("Unexpected asynchronous exception at : " + method.getDeclaringClass().getName() + "."
				+ method.getName(), ex);
	}
}
