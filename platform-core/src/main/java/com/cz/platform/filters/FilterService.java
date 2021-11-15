package com.cz.platform.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FilterService {

	@Autowired
	@Lazy
	private DynamicFilterDataService dynamicFilterService;

	@Autowired
	@Lazy
	private StaticFilterDataService staticFilterService;

	@Autowired
	@Lazy
	private GenericFilterConfig filterConfig;

	public List<FilterDTO<Object>> getFiltersViaConfigKey(String key) {
		if (!filterConfig.getFilterToBeServed().containsKey(key)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid filter code");
		}
		Set<String> filters = filterConfig.getFilterToBeServed().get(key);
		return getFilters(filters);
	}

	public List<FilterDTO<Object>> getFilters(Set<String> filters) {
		log.debug("get filter listOfFiltersTobeServed : {}", filters);
		List<FilterDTO<Object>> list = new ArrayList<>();
		for (String filterEnum : filters) {
			FilterDTO<Object> filter = getFilter(filterEnum);
			list.add(filter);
		}
		return list;
	}

	public List<FilterDTO<Object>> getFilters(List<String> filters) {
		log.debug("get filter listOfFiltersTobeServed : {}", filters);
		List<FilterDTO<Object>> list = new ArrayList<>();
		for (String filterEnum : filters) {
			FilterDTO<Object> filter = getFilter(filterEnum);
			list.add(filter);
		}
		return list;
	}

	public FilterDTO<Object> getFilter(String filterEnum) {
		FilterDTO<Object> filter = staticFilterService.getFilterData(filterEnum);
		if (ObjectUtils.isEmpty(filter)) {
			filter = dynamicFilterService.getFilterData(filterEnum);
		}
		return filter;
	}

}
