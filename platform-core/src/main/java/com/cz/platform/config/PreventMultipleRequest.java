package com.cz.platform.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface PreventMultipleRequest {
	public boolean isUserSpecific() default true;

	public long ttlInSeconds() default 5L;
}
