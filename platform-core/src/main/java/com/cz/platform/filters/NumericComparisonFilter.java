package com.cz.platform.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

public class NumericComparisonFilter<T> extends AbstractFilter {
	private T value;

	public NumericComparisonFilter(String field, T value, FilterOperationsType comaprison) {
		super(field, comaprison);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "InElasticFilter [value=" + value + ", filterType=" + filterType + ", field=" + field + "]";
	}

	@Override
	public Criteria getCriteria() {
		switch (getFilterType()) {
		case GREATER_THAN:
			return Criteria.where(field).gt(this.value);
		case GREATER_THAN_EQUAL:
			return Criteria.where(field).gte(this.value);
		case LESS_THAN:
			return Criteria.where(field).lt(this.value);
		case LESS_THAN_EQUAL:
			return Criteria.where(field).lte(this.value);
		default:
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filterType for comparisonFilter");
		}
	}

}
