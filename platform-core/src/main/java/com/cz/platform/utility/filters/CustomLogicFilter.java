package com.cz.platform.utility.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class CustomLogicFilter extends AbstractFilter {

	public CustomLogicFilter(String field) {
		super(field, FilterOperationsType.CUSTOM_TYPE);
	}

	@Override
	public Criteria getCriteria() {
		throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid configuration");
	}

}
