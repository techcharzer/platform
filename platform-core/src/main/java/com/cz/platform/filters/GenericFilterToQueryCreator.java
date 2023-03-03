package com.cz.platform.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenericFilterToQueryCreator {

	protected final Map<String, Function<AbstractFilter, Criteria>> MAP_OF_FILTER_TO_QUERY_MAPPER = new HashMap<>();

	public Criteria getFilter(List<AbstractFilter> filters) {
		log.debug("abstract filters : {}", filters);
		if (ObjectUtils.isEmpty(filters)) {
			return null;
		}
		Criteria criteria = new Criteria();
		List<Criteria> listOfCriterias = new ArrayList<>();
		for (AbstractFilter filter : filters) {
			if (MAP_OF_FILTER_TO_QUERY_MAPPER.containsKey(filter.getField())) {
				Criteria subCriteria = MAP_OF_FILTER_TO_QUERY_MAPPER.get(filter.getField()).apply(filter);
				if (!ObjectUtils.isEmpty(subCriteria)) {
					listOfCriterias.add(subCriteria);
				}
			} else {
				listOfCriterias.add(filter.getCriteria());
			}
		}
		Criteria[] array = listOfCriterias.toArray(new Criteria[listOfCriterias.size()]);
		criteria.andOperator(array);
		return criteria;
	}

}
