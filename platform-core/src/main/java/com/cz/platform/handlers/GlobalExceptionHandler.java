package com.cz.platform.handlers;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.ErrorField;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField MethodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
		String response = MessageFormat.format("parameter {0} is invalid", e.getName());
		LOG.error("methodArgumentTypeMismatchException occured: ", e);
		ErrorField field = new ErrorField(PlatformExceptionCodes.INVALID_DATA.getCode(), response);
		return field;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField httpReadableException(HttpMessageNotReadableException e) {
		LOG.error("HttpMessageNotReadableException occured: ", e);
		ErrorField field = new ErrorField(PlatformExceptionCodes.INVALID_DATA.getCode(), e.getMessage());
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
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorField accessDeniedException(AccessDeniedException e) {
		LOG.error("AccessDeniedException occured: ", e);
		ErrorField errorField = new ErrorField(PlatformExceptionCodes.ACCESS_DENIED.getCode(),
				PlatformExceptionCodes.ACCESS_DENIED.getMessage());
		return errorField;
	}

	@ExceptionHandler(JsonParseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField exception(JsonParseException e) {
		LOG.error("JsonParseException occured: ", e);
		String message = MessageFormat.format("Invalid JSON. {0}", e.getMessage());
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
		return field;
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField exception(InvalidFormatException e) {
		LOG.error("InvalidFormatException occured: ", e);
		String message = e.getMessage();
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
		return field;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField exception(RuntimeException e) {
		LOG.error("RuntimeException occured: ", e);
		String message = "Some error occurred please try again later.";
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
		return field;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField exception(Exception e) {
		LOG.error("Exception occured: ", e);
		String message = "Some error occurred please try again later.";
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
		return field;
	}

}
