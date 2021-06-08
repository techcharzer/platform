package com.cz.platform.exception;

public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 198653274382982L;
	private ErrorField error;

	public ErrorField getError() {
		return error;
	}

	public ApplicationException(String code, String message, Exception e) {
		this(code.concat(":").concat(message), code, message, e);
	}

	public ApplicationException(String code, String message) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message);
	}

	public ApplicationException(String exceptionName, String code, String message, Exception e) {
		super(exceptionName, e);
		error = new ErrorField(code, message);
	}

	public ApplicationException(IExceptionCodes exception, Exception e) {
		this(exception.getCode(), exception.getMessage(), e);
	}

	public ApplicationException(IExceptionCodes exception) {
		this(exception.getCode(), exception.getMessage());
	}

}
