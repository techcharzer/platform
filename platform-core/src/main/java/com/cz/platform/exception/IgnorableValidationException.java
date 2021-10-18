package com.cz.platform.exception;

public class IgnorableValidationException extends RuntimeException {

	private static final long serialVersionUID = 73873457682L;
	private ErrorField error;

	public ErrorField getError() {
		return error;
	}

	public IgnorableValidationException(String code, String message) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message);
	}

	public IgnorableValidationException(IExceptionCodes exception) {
		this(exception.getCode(), exception.getMessage());
	}

}
