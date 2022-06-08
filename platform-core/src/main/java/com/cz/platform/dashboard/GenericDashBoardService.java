package com.cz.platform.dashboard;

import java.util.HashMap;
import java.util.Map;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericDashBoardService {

	protected static final Map<String, CustomDasboardCardFetcher> MAP_OF_DATA_FETCHERS = new HashMap<>();

	@FunctionalInterface
	public interface CustomDasboardCardFetcher {
		DashboardCardDTO fetchData(String userId);
	}

	public DashboardCardDTO getDashBoardCardDTO(String key, String userId) {
		if (!MAP_OF_DATA_FETCHERS.containsKey(key)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid cron key for execution");
		}
		try {
			return MAP_OF_DATA_FETCHERS.get(key).fetchData(userId);
		} catch (Exception e) {
			log.error("error occured while executing the cron: {}", key, e);
			throw new ApplicationException(PlatformExceptionCodes.INVALID_DATA.getCode(), e.getMessage());
		}
	}

}
