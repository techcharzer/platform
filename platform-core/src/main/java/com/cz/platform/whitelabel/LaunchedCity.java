package com.cz.platform.whitelabel;

import com.cz.platform.dto.GeoCoordinatesDTO;

import lombok.Data;

@Data
public class LaunchedCity {
	private String cityName;
	private GeoCoordinatesDTO coordinates;
}