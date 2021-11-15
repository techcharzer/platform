package com.cz.platform.filters;

import java.util.HashMap;
import java.util.Map;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GenericEntityToQueryCreatorFactory {

	protected static final Map<Class, GenericFilterToQueryCreator> MAP_CLASS_TO_FILTER_QUERY_CREATOR = new HashMap<>();

	public GenericFilterToQueryCreator getService(Class a) {
		log.info("class name : {}", a);
		if (!MAP_CLASS_TO_FILTER_QUERY_CREATOR.containsKey(a)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid value of the class : " + a.getCanonicalName());
		}
		return MAP_CLASS_TO_FILTER_QUERY_CREATOR.get(a);
	}

}
