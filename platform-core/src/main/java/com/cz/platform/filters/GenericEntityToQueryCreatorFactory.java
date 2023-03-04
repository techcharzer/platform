package com.cz.platform.filters;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GenericEntityToQueryCreatorFactory {

	protected final Map<Class, GenericFilterToQueryCreator> MAP_CLASS_TO_FILTER_QUERY_CREATOR = new HashMap<>();

	public GenericFilterToQueryCreator getService(Class a) {
		if (ObjectUtils.isEmpty(a)) {
			return null;
		}
		log.debug("class name : {}", a.getCanonicalName());
		return MAP_CLASS_TO_FILTER_QUERY_CREATOR.get(a);
	}

}
