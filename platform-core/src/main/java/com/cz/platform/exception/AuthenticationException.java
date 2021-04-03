package com.cz.platform.exception;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 198765467682L;
	private ErrorField error;

	public ErrorField getError() {
		return error;
	}

	public AuthenticationException(String code, String message) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message);
	}

	public AuthenticationException(IExceptionCodes exception) {
		this(exception.getCode(), exception.getMessage());
	}

}
