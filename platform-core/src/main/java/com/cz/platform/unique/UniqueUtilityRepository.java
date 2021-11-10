package com.cz.platform.unique;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class UniqueUtilityRepository {

	private MongoTemplate mongoTemplate;

	public void save(UniqueUtilityEntity entity) {
		if (ObjectUtils.isEmpty(entity)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid entity");
		}
		if (ObjectUtils.isEmpty(entity.getId())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid id");
		}
		mongoTemplate.save(entity, PlatformConstants.UNIQUE_ID_INFO);
	}

	public List<UniqueUtilityEntity> findAll() {
		return mongoTemplate.findAll(UniqueUtilityEntity.class, PlatformConstants.UNIQUE_ID_INFO);
	}
}
