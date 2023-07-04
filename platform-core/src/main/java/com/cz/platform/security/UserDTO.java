package com.cz.platform.security;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.enums.LogInFrom;
import com.cz.platform.enums.OperatingSystem;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

public interface UserDTO {
	public String getUserId();

	public String getMobileNumber();

	public List<RoleDTO> getRoles();

	public ZoneId getZoneId();

	public LogInFrom getLogInFrom();

	public default boolean isLogInFrom(LogInFrom logInFrom) {
		return ObjectUtils.isEmpty(logInFrom) ? false : getLogInFrom().equals(logInFrom);
	}

	public default void validateLogInFrom(LogInFrom logInFrom) {
		if (BooleanUtils.isFalse(isLogInFrom(logInFrom))) {
			String message = MessageFormat.format("User has not logged from : {0}", logInFrom.name());
			throw new ValidationException(PlatformExceptionCodes.ACCESS_DENIED.getCode(), message);
		}
	}

	public default void validateCZOAccess() {
		if (BooleanUtils.isFalse(isLogInFrom(LogInFrom.CZO))) {
			throw new ValidationException(PlatformExceptionCodes.ACCESS_DENIED.getCode(), "Does not have CZO access");
		}
	}

	public String getChargePointOperatorId();

	public OperatingSystem getOperatingSystem();
}
