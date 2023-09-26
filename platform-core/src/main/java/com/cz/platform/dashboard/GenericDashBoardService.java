package com.cz.platform.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericDashBoardService {

	protected static final Map<String, CustomDasboardCardFetcher> MAP_OF_DATA_FETCHERS = new HashMap<>();

	@FunctionalInterface
	public interface CustomDasboardCardFetcher {
		DashboardCardDTO fetchData(IFetchDashboardRequest request);
	}

	public DashboardCardDTO getDashBoardCardDTO(IFetchDashboardRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		if (ObjectUtils.isEmpty(request.getKey())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid key");
		}
		if (!MAP_OF_DATA_FETCHERS.containsKey(request.getKey())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid key for fetching dashboard");
		}
		try {
			return MAP_OF_DATA_FETCHERS.get(request.getKey()).fetchData(request);
		} catch (Exception e) {
			log.error("error occured while fetching the dashboard: {}", request, e);
			throw new ApplicationException(PlatformExceptionCodes.INVALID_DATA.getCode(), e.getMessage());
		}
	}

	public static interface IFetchDashboardRequest {
		String getKey();
	}

}
