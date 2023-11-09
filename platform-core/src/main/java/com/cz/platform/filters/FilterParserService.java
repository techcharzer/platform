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

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.dto.Range;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.utility.CommonUtility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public final class FilterParserService {

	private GenericFilterConfig filterConfig;

	private static final Map<FilterOperationsType, BiFunction<String, List<String>, AbstractFilter>> MAP_OF_FILTER_PARSING = new HashMap<>();

	@PostConstruct
	private void fillMap() {
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.IN, this::inFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.RANGE, this::rangeFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.NEAR_TO, this::nearToFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.DATE_RANGE, this::dateRangeFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.GREATER_THAN, this::gtFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.LESS_THAN, this::ltFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.GREATER_THAN_EQUAL, this::gteFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.LESS_THAN_EQUAL, this::lteFilterParsing);
		MAP_OF_FILTER_PARSING.put(FilterOperationsType.SINGLE_DATE_RANGE, this::singleDateRangeFilterParsing);
	}

	private AbstractFilter inFilterParsing(String field, List<String> value) {
		AbstractFilter filter = new InFilter<String>(field, value);
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		return filter;
	}

	private AbstractFilter gtFilterParsing(String field, List<String> value) {
		return numericComparisonFilterParsing(field, value, FilterOperationsType.GREATER_THAN);
	}

	private AbstractFilter gteFilterParsing(String field, List<String> value) {
		return numericComparisonFilterParsing(field, value, FilterOperationsType.GREATER_THAN_EQUAL);
	}

	private AbstractFilter ltFilterParsing(String field, List<String> value) {
		return numericComparisonFilterParsing(field, value, FilterOperationsType.LESS_THAN);
	}

	private AbstractFilter lteFilterParsing(String field, List<String> value) {
		return numericComparisonFilterParsing(field, value, FilterOperationsType.LESS_THAN_EQUAL);
	}

	private AbstractFilter numericComparisonFilterParsing(String field, List<String> value,
			FilterOperationsType opType) {
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		if (value.size() != 1) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. Only 1 gt/gte/lt/lte value is allowed");
		}
		if (ObjectUtils.isEmpty(opType)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid opType");
		}
		Double val = Double.parseDouble(value.get(0));
		return new NumericComparisonFilter<Double>(field, val, FilterOperationsType.GREATER_THAN_EQUAL);
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

	private AbstractFilter singleDateRangeFilterParsing(String field, List<String> value) {
		if (ObjectUtils.isEmpty(value)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter parameters. requestparam or requestparam value is empty.");
		}
		for (String range : value) {
			Range<Long> r = CommonUtility.parseRangeLong(range, "-");
			if (r != null) {
				Range<Instant> rangeVal = new Range<Instant>(Instant.ofEpochMilli(r.getFrom()),
						Instant.ofEpochMilli(r.getTo()));
				return new SingleDateRangeFilter(field, rangeVal);
			}
		}
		return null;
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

	public RequestParams parseRequestParams(MultiValueMap<String, String> map) {
		RequestParams params = new RequestParams();

		log.debug("query params : {}", map);
		List<AbstractFilter> filters = new ArrayList<AbstractFilter>();

		for (String key : map.keySet()) {
			try {
				if (StringUtils.equals("downloadRequest", key)) {
					params.setDownloadRequest(true);
				} else {
					AbstractFilter filter = getFilter(key, map.get(key));
					if (!ObjectUtils.isEmpty(filter)) {
						filters.add(filter);
					}
				}
			} catch (Exception e) {
				log.debug("parsing failed for {} fail fast enabled : {}", key, filterConfig.getFailFast());
				if (filterConfig.getFailFast()) {
					throw e;
				}
			}
		}
		log.debug("filters parsed: {}", filters);
		params.setFilters(filters);
		return params;
	}

	@Data
	public static class RequestParams {
		private boolean isDownloadRequest;
		private List<AbstractFilter> filters;
	}

	public List<AbstractFilter> parseQueryParams(MultiValueMap<String, String> map) {
		RequestParams params = parseRequestParams(map);
		return params.getFilters();
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
			Set<String> allowedFilters = new HashSet<>();
			for (Entry<String, Set<String>> entry : filterConfig.getFilterToBeServed().entrySet()) {
				allowedFilters.addAll(entry.getValue());
			}
			if (ObjectUtils.isEmpty(field) || !allowedFilters.contains(field)) {
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