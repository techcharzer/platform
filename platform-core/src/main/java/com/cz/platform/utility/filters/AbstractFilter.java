package com.cz.platform.utility.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "filterType")
@JsonSubTypes({ @Type(value = InFilter.class, name = "IN"), @Type(value = RangeFilter.class, name = "RANGE") })
public abstract class AbstractFilter {
	public String field;
	public FilterOperationsType filterType;

	public abstract Criteria getCriteria();
}
