package com.cz.platform.utility.filters;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreType
@Getter
@Setter
public class NearFilter extends AbstractFilter {

	private GeoCoordinatesDTO location;
	private Double maxDistance;
	private Double minDistance = 0.0D;

	public NearFilter(String field) {
		super(field, FilterOperationsType.NEAR_TO);
	}

	@Override
	public Criteria getCriteria() {
		throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid configuration");
	}

}
