package org.charzer.platform.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.charzer.platform.exception.PlatFormExceptionCodes;
import org.charzer.platform.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheCommonService {

	@Autowired
	@Lazy
	CacheCommonService cacheCommonService;

	@FunctionalInterface
	private interface AnonymousMethod {
		void execute();
	}

	protected static final Map<String, AnonymousMethod> REFRESH_KEY_METHOD = new HashMap<>();

	public void refreshCache(List<String> cacheIds) {
		if (ObjectUtils.isEmpty(cacheIds)) {
			return;
		}
		validateRequest(cacheIds);
		for (String keys : cacheIds) {
			log.info("clearing cache for key : {}", keys);
			REFRESH_KEY_METHOD.get(keys).execute();
		}
	}

	private void validateRequest(List<String> cacheIds) {
		for (String key : cacheIds) {
			if (!REFRESH_KEY_METHOD.containsKey(key)) {
				throw new ValidationException(PlatFormExceptionCodes.INVALID_DATA.getCode(),
						"Invalid cacheId : " + key);
			}
		}
	}

}
