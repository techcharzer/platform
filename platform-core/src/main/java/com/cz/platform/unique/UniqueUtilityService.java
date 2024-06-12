package com.cz.platform.unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.LoggerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.utility.PlatformCommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UniqueUtilityService {

	@Autowired
	@Qualifier(PlatformConstants.REDIS_TEMPLATE_FOR_UNIQUE_NUMBERS)
	private RedisTemplate<String, Integer> redisTemplate;
	private static final String NEW_LOGIC_KEY = "NEW_LOGIC_KEY_V1";
	private RedissonClient redissonClient;
	private static final String BASE_SALT = "23456789abcdefghijkmnpqrstuvwxyz";
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

	public String getUniqueId(String basePath) {
		Long val = getNextNumber(basePath, true, random.nextInt(100));
		return getString(val);
	}

	public String getNextId(String basePath) {
		Long val = getNextNumber(basePath, true, 1);
		return getString(val);
	}

	public Long getNextNumber(String basePath) {
		return getNextNumber(basePath, false, 1);
	}

	public Long getNextNumber(String basePath, boolean persistInDB) {
		return getNextNumber(basePath, persistInDB, 1);
	}

	private Long getNextNumber(String basePath, boolean persistInDB, Integer count) {
		RAtomicLong atomicLong = redissonClient.getAtomicLong(NEW_LOGIC_KEY);
		long newLogic = atomicLong.get();
		if (newLogic > 0) {
			return getNextNumberV2(basePath);
		} else {
			BoundHashOperations<String, String, Integer> x = redisTemplate.boundHashOps(basePath);
			Long val = x.increment(basePath, count);
			save(basePath, val);
			return val;
		}
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

	public void save(String basePath, Long value) {
		UniqueUtilityEntity obj = new UniqueUtilityEntity();
		obj.setId(basePath);
		obj.setValue(value);
		mongoTemplate.save(obj);
		log.info("saved unique object: {}", obj);
	}

	private MongoTemplate mongoTemplate;
	private PlatformCommonService platformCommonService;

	public Long getNextNumberV2(String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(id);
		query.addCriteria(criteria);
		Update update = new Update();
		update.inc("value", 1L);
		mongoTemplate.updateFirst(query, update, UniqueUtilityEntity.class);
		UniqueUtilityEntity value = mongoTemplate.findById(id, UniqueUtilityEntity.class);
		if (ObjectUtils.isEmpty(value)) {
			value = new UniqueUtilityEntity();
			value.setId(id);
			value.setValue(1L);
			platformCommonService.takeLock("CREATE_NEW_KEY" + id, 1, "key generation failed try again after 2 seconds",
					LoggerType.ERROR);
			mongoTemplate.save(value);
		}
		log.info("auto increment: {}", value);
		return value.getValue();
	}

	public void enableNewLogic() {
		RAtomicLong atomicLong = redissonClient.getAtomicLong(NEW_LOGIC_KEY);
		atomicLong.addAndGet(10);
		log.info("new logic in place");
	}

}
