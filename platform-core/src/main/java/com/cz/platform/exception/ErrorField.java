package com.cz.platform.exception;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class ErrorField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 13234548716666666L;
	private String code;
	private String errorMessage;

	public ErrorField(String code, String errors) {
		this.code = code;
		this.errorMessage = errors;
	}

	public void setErrorMessage(String error) {
		this.errorMessage = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ErrorField(IExceptionCodes codes) {
		this.code = codes.getCode();
		this.errorMessage = codes.getMessage();
	}

}
