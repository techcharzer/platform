package com.cz.platform.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UniqueUtility {

	@Autowired
	@Qualifier(PlatformConstants.REDIS_TEMPLATE_FOR_UNIQUE_NUMBERS)
	private RedisTemplate<String, Integer> redisTemplate;

	private static final String BASE_SALT = "0123456789abcdefghijklmnopqrstuvwxyz";
	private static final Map<Long, Character> MAP_OF_INTEGER_TO_CHARACTER = new HashMap<>();

	private static final Random random = new Random();

	static {
		populate();
	}

	private static void populate() {
		long i = 0;
		for (Character c : BASE_SALT.toCharArray()) {
			MAP_OF_INTEGER_TO_CHARACTER.put(i, c);
			++i;
		}
	}

	public String getUniqueSmallString(String basePath) {
		BoundHashOperations<String, String, Integer> x = redisTemplate.boundHashOps(basePath);
		Long val = x.increment(basePath, random.nextInt(1000));
		return getString(val);
	}

	private String getString(Long val) {
		Long copyVal = val;
		if (ObjectUtils.isEmpty(val)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Unable to generate the unique random string");
		}
		StringBuilder sb = new StringBuilder();
		int base = BASE_SALT.length();
		while (val > 0) {
			long modulo = val % base;
			sb.append(MAP_OF_INTEGER_TO_CHARACTER.get(modulo));
			val = val / base;
		}
		log.debug("unique string generated : {} for number {}", sb.toString(), copyVal);
		return sb.reverse().toString();
	}

}
