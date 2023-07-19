package com.cz.platform.utility;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.LoggerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PlatformCommonService {

	private ObjectMapper mapper;
	private RedissonClient redissonClient;

	public boolean handle404Error(String errorResponse) {
		JsonNode node = null;
		try {
			node = mapper.readTree(errorResponse);
		} catch (JsonProcessingException e) {
			return false;
		}
		if (node != null && node.has("code") && node.get("code").asText().equals(PlatformConstants.CODE_404)) {
			return true;
		}
		return false;
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
