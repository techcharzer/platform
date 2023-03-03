package com.cz.platform.filters;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenericEntityToQueryCreatorFactory {

	protected final Map<Class, GenericFilterToQueryCreator> MAP_CLASS_TO_FILTER_QUERY_CREATOR = new HashMap<>();
	
	@Autowired
	private GenericFilterToQueryCreator defaultQueryCreator;

	public GenericFilterToQueryCreator getService(Class a) {
		if (ObjectUtils.isEmpty(a)) {
			return null;
		}
		log.debug("class name : {}", a.getCanonicalName());
		if (!MAP_CLASS_TO_FILTER_QUERY_CREATOR.containsKey(a)) {
			return defaultQueryCreator;
		}
		return MAP_CLASS_TO_FILTER_QUERY_CREATOR.get(a);
	}

}
