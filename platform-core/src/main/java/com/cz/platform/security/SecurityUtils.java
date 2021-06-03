package com.cz.platform.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;

public class SecurityUtils {

	public static UserLoggedInDTO getLoggedInUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (ObjectUtils.isEmpty(context)) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		}
		if (ObjectUtils.isEmpty(context.getAuthentication())) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		}
		if (ObjectUtils.isEmpty(context.getAuthentication().getPrincipal())) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		}
		return (UserLoggedInDTO) context.getAuthentication().getPrincipal();
	}
}
