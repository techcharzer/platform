package com.cz.platform.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.functionalInterface.AnonymousMethod;

public interface ICacheCommonService {

	static final Map<String, AnonymousMethod> REFRESH_KEY_METHOD = new HashMap<>();

	static void refreshCache(List<String> cacheIds) {
		if (ObjectUtils.isEmpty(cacheIds)) {
			return;
		}
		validateRequest(cacheIds);
		for (String keys : cacheIds) {
			REFRESH_KEY_METHOD.get(keys).execute();
		}
	}

	static void validateRequest(List<String> cacheIds) {
		for (String key : cacheIds) {
			if (!REFRESH_KEY_METHOD.containsKey(key)) {
				throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
						"Invalid cacheId : " + key);
			}
		}
	}

	static Set<String> refreshCacheKeys() {
		return REFRESH_KEY_METHOD.keySet();
	}

}
