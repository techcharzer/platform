package com.cz.platform.filters;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

public interface GenericEntityToQueryCreatorFactory {

	final static Map<Class, GenericFilterToQueryCreator> MAP_CLASS_TO_FILTER_QUERY_CREATOR = new HashMap<>();

	static GenericFilterToQueryCreator getService(Class a) {
		if (ObjectUtils.isEmpty(a)) {
			return null;
		}
		return MAP_CLASS_TO_FILTER_QUERY_CREATOR.get(a);
	}

	static void add(Class classs, GenericFilterToQueryCreator queryCreator) {
		MAP_CLASS_TO_FILTER_QUERY_CREATOR.put(classs, queryCreator);
	}

}
