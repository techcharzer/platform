package com.cz.platform.filters;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.dto.Range;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.utility.CommonUtility;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public final class FilterParserService {

	private GenericFilterConfig filterConfig;

	private static final Map<FilterOperationsType, BiFunction<String, List<String>, AbstractFilter>> MAP_OF_FILTER_PARSING = new HashMap<>();
	private static final Set<String> ALLOWED_FILTERS = new HashSet<>();

	@PostConstruct
	private void fillMap() {
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.IN, this::inFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.RANGE, this::rangeFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.NEAR_TO, this::nearToFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.DATE_RANGE, this::dateRangeFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.CUSTOM_TYPE, this::customFilterParsing);
		for (Entry<String, Set<String>> entry : filterConfig.getFilterToBeServed().entrySet()) {
			ALLOWED_FILTERS.addAll(entry.getValue());
		}
	}

	private AbstractFilter inFilterParsing(String field, List<String> value) {
		AbstractFilter filter = new InFilter<String>(field, value);
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		return filter;
	}

	private AbstractFilter rangeFilterParsing(String field, List<String> value) {
		List<Range<Integer>> ranges = new ArrayList<>();
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		for (String range : value) {
			Range<Integer> r = CommonUtility.parseRange(range, "-");
			if (r != null) {
				ranges.add(r);
			}
		}
		return new RangeFilter<Integer>(field, ranges);
	}

	private AbstractFilter dateRangeFilterParsing(String field, List<String> value) {
		List<Range<Instant>> ranges = new ArrayList<>();
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		for (String range : value) {
			Range<Long> r = CommonUtility.parseRangeLong(range, "-");
			if (r != null) {
				ranges.add(new Range<Instant>(Instant.ofEpochMilli(r.getFrom()), Instant.ofEpochMilli(r.getTo())));
			}
		}
		return new DateRangeFilter(field, ranges);
	}

	private AbstractFilter nearToFilterParsing(String field, List<String> value) {
		if (ObjectUtils.isEmpty(value) || value.size() > 1) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Only one near to location can be sent.");
		}
		String val = value.get(0);
		String[] vals = val.split(",");
		GeoCoordinatesDTO point = new GeoCoordinatesDTO();
		point.setLat(Double.parseDouble(vals[0]));
		point.setLon(Double.parseDouble(vals[1]));
		Double maxDistance = Double.parseDouble(vals[2]);
		if (vals.length < 3) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Format keyName-nearTo=longitude,latitude,maxdistance[,mindistance]");
		}
		Double minDistance = 0.0D;
		if (vals.length > 3) {
			minDistance = Double.parseDouble(vals[3]);
		}

		NearFilter filter = new NearFilter(field);
		filter.setFilterType(FilterOperationsType.NEAR_TO);
		filter.setLocation(point);
		filter.setMaxDistance(maxDistance);
		filter.setMinDistance(minDistance);
		return filter;
	}

	private AbstractFilter customFilterParsing(String field, List<String> value) {
		return new CustomLogicFilter(field);
	}

	public List<AbstractFilter> parseQueryParams(MultiValueMap<String, String> map) {
		log.debug("query params : {}", map);
		List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
		try {
			for (String key : map.keySet()) {
				AbstractFilter filter = getFilter(key, map.get(key));
				if (!ObjectUtils.isEmpty(filter)) {
					filters.add(filter);
				}
			}
		} catch (Exception e) {
			log.debug("fail fast enabled : {}", filterConfig.getFailFast());
			if (filterConfig.getFailFast()) {
				throw e;
			}
		}
		return filters;
	}

	private AbstractFilter getFilter(String key, List<String> value) {
		if (ObjectUtils.isEmpty(key)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		String arr[] = key.split("-");
		if (arr.length > 2) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters format.");
		}
		String field = arr[0];
		if (ObjectUtils.isEmpty(field)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid field");
		}
		// exclude page and size and other params if they needs to be passed down
		AbstractFilter filter = null;
		if (!filterConfig.getExcludedParams().contains(field)) {
			if (ObjectUtils.isEmpty(field) || !ALLOWED_FILTERS.contains(field)) {
				throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
						MessageFormat.format("filter {0} type not allowed", field));
			}
			FilterOperationsType filterOperationsType = arr.length == 1 ? FilterOperationsType.IN
					: FilterOperationsType.getFilterType(arr[1]);
			if (!MAP_OF_FILTER_PARSING.containsKey(filterOperationsType)) {
				throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid filter type.");
			}
			filter = MAP_OF_FILTER_PARSING.get(filterOperationsType).apply(field, value);
		}
		return filter;
	}

}