package com.cz.platform.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilterOperationsType {
	RANGE("range"), IN("in"), GREATER_THAN("gt"), LESS_THAN("lt"), GREATER_THAN_EQUAL("gte"), LESS_THAN_EQUAL("lte"),
	CUSTOM_TYPE("cl"), NEAR_TO("nearTo"), DATE_RANGE("dateRange"), SINGLE_DATE_RANGE("singleDateRange");

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
