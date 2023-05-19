package com.cz.platform.filters;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.functionalInterface.AnonymousFunctionV3;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DTOFilterService<T> {

	protected final Map<String, AnonymousFunctionV3<AbstractFilter, List<T>, Set<Integer>, Set<Integer>>> MAP_OF_FILTER_TO_QUERY_MAPPER = new HashMap<>();

	protected abstract void fillMap();

	public final List<T> filter(List<AbstractFilter> filters, List<T> data) {
		log.debug("size before applying filter: {}", data.size());
		if (ObjectUtils.isEmpty(filters)) {
			return data;
		}
		Set<Integer> inclusions = new HashSet<>();
		for (AbstractFilter filter : filters) {
			if (MAP_OF_FILTER_TO_QUERY_MAPPER.containsKey(filter.getField())) {
				List<T> internalList = new LinkedList<>();
				inclusions = MAP_OF_FILTER_TO_QUERY_MAPPER.get(filter.getField()).execute(filter, data, inclusions);
				log.debug("inclusions found for : {}, {}", filter.getField(), inclusions);
				if (ObjectUtils.isEmpty(inclusions)) {
					return Collections.emptyList();
				}
				for (Integer index : inclusions) {
					internalList.add(data.get(index));
				}
				data = internalList;
				inclusions.clear();
			}
		}
		log.debug("size after applying filter: {}", data.size());
		return data;
	}

}
