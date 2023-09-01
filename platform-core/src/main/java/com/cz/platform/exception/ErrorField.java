package com.cz.platform.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorField implements Serializable {

	private static final long serialVersionUID = 13234548716666666L;
	private String code;
	private String message;
	private ErrorType errorType;

	public ErrorField(IExceptionCodes codes, ErrorType errorType) {
		this.code = codes.getCode();
		this.message = codes.getMessage();
		this.errorType = errorType;
	}

	public enum ErrorType {
		VALIDATION_EXCEPTION, APPLICATION_EXCEPTION, AUTHENTICATION_EXCEPTION
	}

}
