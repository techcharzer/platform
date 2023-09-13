package com.cz.platform.exception;

import com.cz.platform.exception.ErrorField.ErrorType;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 198765467682L;
	private ErrorField error;
	private LoggerType logType = LoggerType.ERROR;

	public ErrorField getError() {
		return error;
	}

	public ValidationException(String code, String message) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message, ErrorType.VALIDATION_EXCEPTION);
	}

	public ValidationException(String code, String message, LoggerType loggerType) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message, ErrorType.VALIDATION_EXCEPTION);
		this.logType = loggerType;
	}

	public ValidationException(IExceptionCodes exception) {
		this(exception.getCode(), exception.getMessage());
	}

	public LoggerType getLogType() {
		return logType;
	}

	public void setLogType(LoggerType logType) {
		this.logType = logType;
	}

}
