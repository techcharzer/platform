package com.cz.platform.unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UniqueUtilityService {

	@Autowired
	private MongoTemplate mongoTemplate;
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
		Long val = getNextNumber(basePath, random.nextInt(100));
		return getString(val);
	}

	public String getNextId(String basePath) {
		Long val = getNextNumber(basePath, 1);
		return getString(val);
	}

	public Long getNextNumber(String basePath) {
		return getNextNumber(basePath, 1);
	}

	private Long getNextNumber(String basePath, Integer count) {
		Query query = new Query(Criteria.where("_id").is(basePath));
		Update update = new Update().inc("value", count);
		AutoIncrementEntity updatedDocument = mongoTemplate.findAndModify(query, update,
				FindAndModifyOptions.options().returnNew(true).upsert(true), AutoIncrementEntity.class,
				"auto_increment_entity");
		return updatedDocument.getValue();
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
