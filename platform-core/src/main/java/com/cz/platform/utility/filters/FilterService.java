package com.cz.platform.utility.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.ApplicationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class FilterService {

	private DynamicFilterDataService dynamicFilterService;

	private StaticFilterDataService staticFilterService;

	public FilterDTO<Object> getFilter(IFilterType filterEnum) throws ApplicationException {
		FilterDTO<Object> filter = staticFilterService.getFilterData(filterEnum);
		if (ObjectUtils.isEmpty(filter)) {
			filter = dynamicFilterService.getFilterData(filterEnum);
		}
		return filter;
	}

	public List<FilterDTO<Object>> getFilters(List<IFilterType> mobileFilters) throws ApplicationException {
		log.debug("get filter listOfFiltersTobeServed : {}", mobileFilters);
		List<FilterDTO<Object>> list = new ArrayList<>();
		for (IFilterType filterEnum : mobileFilters) {
			FilterDTO<Object> filter = getFilter(filterEnum);
			list.add(filter);
		}
		return list;
	}

}
