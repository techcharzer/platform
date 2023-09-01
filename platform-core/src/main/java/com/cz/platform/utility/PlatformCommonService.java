package com.cz.platform.utility;

import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.ErrorField;
import com.cz.platform.exception.LoggerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public final class PlatformCommonService {

	private ObjectMapper mapper;
	private RedissonClient redissonClient;

	public ErrorField parseError(String errorResponse) {
		try {
			return mapper.readValue(errorResponse, ErrorField.class);
		} catch (JsonProcessingException e) {
			log.error("error occured while parsing the error message", errorResponse);
		}
		return null;
	}

	public boolean is404Error(String errorResponse) {
		ErrorField error = parseError(errorResponse);
		if (ObjectUtils.isEmpty(error)) {
			return false;
		}
		return StringUtils.equals(error.getCode(), PlatformConstants.CODE_404);
	}

	public void throwRespectiveError(String errorResponse) {
		ErrorField error = parseError(errorResponse);
		if (ObjectUtils.isEmpty(error)) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}
		switch (error.getErrorType()) {
		case APPLICATION_EXCEPTION:
			throw new ApplicationException(error.getCode(), error.getMessage());
		case VALIDATION_EXCEPTION:
			throw new ApplicationException(error.getCode(), error.getMessage());
		case AUTHENTICATION_EXCEPTION:
			throw new AuthenticationException(error.getCode(), error.getMessage());
		default:
			break;
		}
	}

	public RLock takeLock(String key, long leaseTimeInSeconds) {
		return takeLock(key, leaseTimeInSeconds, "Request being processed, please wait...", LoggerType.ERROR);
	}

	public RLock takeLock(String key, long leaseTimeInSeconds, String errorMessage, LoggerType loggerType) {
		RLock lock = redissonClient.getLock(key);
		if (lock.isLocked()) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), errorMessage, loggerType);
		}
		lock.lock(leaseTimeInSeconds, TimeUnit.SECONDS);
		return lock;
	}

	public void unlock(String key) {
		RLock lock = redissonClient.getLock(key);
		lock.unlock();
	}

	public void unlock(RLock lock) {
		lock.unlock();
	}

	public void forceUnlock(String key) {
		RLock lock = redissonClient.getLock(key);
		lock.forceUnlock();
	}

	public void forceUnlock(RLock lock) {
		lock.forceUnlock();
	}
}
