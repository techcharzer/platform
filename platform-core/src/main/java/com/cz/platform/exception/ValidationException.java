package com.cz.platform.exception;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 198765467682L;
	private ErrorField error;
	private boolean logItAsError = true;

	public ErrorField getError() {
		return error;
	}

	public ValidationException(String code, String message) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message);
	}
	
	public ValidationException(String code, String message, boolean logItAsError) {
		super(code.concat(":").concat(message));
		error = new ErrorField(code, message);
		this.logItAsError = logItAsError;
	}

	public ValidationException(IExceptionCodes exception) {
		this(exception.getCode(), exception.getMessage());
	}

	public boolean isLogItAsError() {
		return logItAsError;
	}

	public void setLogItAsError(boolean logItAsError) {
		this.logItAsError = logItAsError;
	}

}
