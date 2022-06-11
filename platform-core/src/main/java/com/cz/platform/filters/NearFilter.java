package com.cz.platform.filters;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.dto.GeoCoordinatesDTO;
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
		GeoJsonPoint point = new GeoJsonPoint(this.getLocation().getLon(), this.getLocation().getLat());
		return Criteria.where(this.field).near(point).maxDistance(this.getMaxDistance())
				.minDistance(this.getMinDistance());
	}

}
