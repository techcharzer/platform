package com.cz.platform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatFormExceptionCodes implements IExceptionCodes {

	INVALID_DATA("PL_1000", "Invalid/empty data requested"),
	INTERNAL_SERVER_ERROR("PL_1001", "Some Internal Server Error occured"),
	SERVICE_NOT_WORKING("PL_1002", "Some Internal dependent service not working"),
	AUTHENTICATION_CODE("PL_1003", "Authentication/authorization failure"),
	ACCESS_DENIED("PL_1004", "Authentication/authorization failure");

	private String code;
	private String message;
}
