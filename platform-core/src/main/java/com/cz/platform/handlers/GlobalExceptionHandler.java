package com.cz.platform.handlers;

import java.text.MessageFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.ErrorField;
import com.cz.platform.exception.ErrorField.ErrorType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.UserDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField MethodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e,
			HttpServletRequest request) {
		String requestLog = logRequest(request);
		String response = MessageFormat.format("parameter {0} is invalid", e.getName());
		LOG.error("methodArgumentTypeMismatchException occured: {}", requestLog, e);
		ErrorField field = new ErrorField(PlatformExceptionCodes.INVALID_DATA.getCode(), response,
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

	private String logRequest(HttpServletRequest request) {
		try {
			String method = request.getMethod();
			String path = request.getRequestURI();
			String queryPrams = request.getQueryString();
			StringBuilder builder = new StringBuilder();
			builder.append(method).append(" ").append(path).append(" ");
			if (!ObjectUtils.isEmpty(queryPrams)) {
				builder.append("?").append(queryPrams);
			}
			Optional<String> userIdOptional = logUserId();
			if (userIdOptional.isPresent()) {
				builder.append("for userId ").append(userIdOptional.get());
			}
			return builder.toString();
		} catch (Exception e) {
			LOG.warn("error occured while logging request: {}", request, e);
			return "\nerror occured while logging request\n";
		}
	}

	private Optional<String> logUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (ObjectUtils.isEmpty(context)) {
			return Optional.empty();
		}
		if (ObjectUtils.isEmpty(context.getAuthentication())) {
			return Optional.empty();
		}
		if (ObjectUtils.isEmpty(context.getAuthentication().getPrincipal())) {
			return Optional.empty();
		}
		if (!(context.getAuthentication().getPrincipal() instanceof UserDTO)) {
			return Optional.empty();
		}
		UserDTO user = (UserDTO) context.getAuthentication().getPrincipal();
		return Optional.of(user.getUserId());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField exception(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		String response = MessageFormat.format("Request method '{0}' not supported", e.getMethod());
		LOG.error("HttpRequestMethodNotSupportedException occured: {}", requestLog, e);
		ErrorField field = new ErrorField(PlatformExceptionCodes.INVALID_DATA.getCode(), response,
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField httpReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("HttpMessageNotReadableException occured: {}", requestLog, e);
		ErrorField field = new ErrorField(PlatformExceptionCodes.INVALID_DATA.getCode(), e.getMessage(),
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField validationException(ValidationException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		switch (e.getLogType()) {
		case ERROR:
			LOG.error("ValidationException occured: {}", requestLog, e);
			break;
		case WARN:
			LOG.warn("ValidationException occured: {}", requestLog, e);
			break;
		default:
			break;
		}
		return e.getError();
	}

	@ExceptionHandler(ApplicationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField c2cException(ApplicationException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("ApplicationException occured: {}", requestLog, e);
		return e.getError();
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorField accessDeniedException(AccessDeniedException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("AccessDeniedException occured: {}", requestLog, e);
		ErrorField errorField = new ErrorField(PlatformExceptionCodes.ACCESS_DENIED.getCode(),
				PlatformExceptionCodes.ACCESS_DENIED.getMessage(), ErrorType.AUTHENTICATION_EXCEPTION);
		return errorField;
	}

	@ExceptionHandler(JsonParseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField exception(JsonParseException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("JsonParseException occured: {}", requestLog, e);
		String message = MessageFormat.format("Invalid JSON. {0}", e.getMessage());
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message,
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorField exception(InvalidFormatException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("InvalidFormatException occured: {}", requestLog, e);
		String message = e.getMessage();
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message,
				ErrorType.VALIDATION_EXCEPTION);
		return field;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField exception(RuntimeException e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("RuntimeException occured: {}", requestLog, e);
		String message = "Some error occurred please try again later.";
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message,
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorField exception(Exception e, HttpServletRequest request) {
		String requestLog = logRequest(request);
		LOG.error("Exception occured: {}", requestLog, e);
		String message = "Some error occurred please try again later.";
		ErrorField field = new ErrorField(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message,
				ErrorType.APPLICATION_EXCEPTION);
		return field;
	}

}
