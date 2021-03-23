package com.charzer.platform.handlers;

import java.nio.file.AccessDeniedException;
import java.text.MessageFormat;

import org.charzer.platform.exception.ApplicationException;
import org.charzer.platform.exception.ErrorField;
import org.charzer.platform.exception.PlatFormExceptionCodes;
import org.charzer.platform.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField MethodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
		String response = MessageFormat.format("parameter {0} is invalid", e.getName());
		LOG.error("methodArgumentTypeMismatchException occured: ", e);
		ErrorField field = new ErrorField(PlatFormExceptionCodes.INVALID_DATA.getCode(), response);
		return field;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField httpReadableException(HttpMessageNotReadableException e) {
		LOG.error("HttpMessageNotReadableException occured: ", e);
		ErrorField field = new ErrorField(PlatFormExceptionCodes.INVALID_DATA.getCode(), e.getMessage());
		return field;
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField validationException(ValidationException e) {
		LOG.error("ValidationException occured: {}", e.getError(), e);
		return e.getError();
	}

	@ExceptionHandler(ApplicationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField c2cException(ApplicationException e) {
		LOG.error("ApplicationException occured: ", e);
		return e.getError();
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ErrorField accessDeniedException(AccessDeniedException e) {
		LOG.error("AccessDeniedException occured: ", e);
		ErrorField errorField = new ErrorField(PlatFormExceptionCodes.ACCESS_DENIED.getCode(),
				PlatFormExceptionCodes.ACCESS_DENIED.getMessage());
		return errorField;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField exception(Exception e) {
		LOG.error("Exception occured: ", e);
		String message = "Some error occurred please try again later.";
		ErrorField field = new ErrorField(PlatFormExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
		return field;
	}

}
