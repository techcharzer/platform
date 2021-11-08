package com.cz.platform.utility.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilterOperationsType {
	RANGE("range"), IN("in"), GREATER_THAN("gte"), LESS_THAN("lte"), CUSTOM_TYPE("cl"), NEAR_TO("nearTo"),
	DATE_RANGE("dateRange");

	private String operatorName;

	public static FilterOperationsType getFilterType(String filterType) {
		for (FilterOperationsType filter : FilterOperationsType.values()) {
			if (filter.getOperatorName().equals(filterType)) {
				return filter;
			}
		}
		return null;
	}
}
