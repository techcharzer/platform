package com.cz.platform.whitelabel;

import java.io.Serializable;

import com.cz.platform.dto.GeoCoordinatesDTO;

import lombok.Data;

@Data
public class LaunchedCity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9068734056137142045L;
	private String cityName;
	private GeoCoordinatesDTO coordinates;
}