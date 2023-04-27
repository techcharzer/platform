package com.cz.platform.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreType
@Getter
@Setter
@ToString
public class CustomLogicFilter<T> extends AbstractFilter {

	private T value;

	public CustomLogicFilter(String field, T value) {
		super(field, FilterOperationsType.CUSTOM_TYPE);
		this.value = value;
	}

	@Override
	public Criteria getCriteria() {
		throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid configuration");
	}

}
