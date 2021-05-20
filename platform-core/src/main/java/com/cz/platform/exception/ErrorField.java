package com.cz.platform.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorField implements Serializable {

	private static final long serialVersionUID = 13234548716666666L;
	private String code;
	private String message;

	public ErrorField(IExceptionCodes codes) {
		this.code = codes.getCode();
		this.message = codes.getMessage();
	}

}
