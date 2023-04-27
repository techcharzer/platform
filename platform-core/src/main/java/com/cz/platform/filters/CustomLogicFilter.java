package com.cz.platform.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class CustomLogicFilter<T> extends AbstractFilter {

	private T val;

	public CustomLogicFilter(String field, T val) {
		super(field, FilterOperationsType.CUSTOM_TYPE);
		this.val = val;
	}

	@Override
	public Criteria getCriteria() {
		throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid configuration");
	}

}
