package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Hub implements GroupConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 72567725763954329L;
	private String hubName;
	private GeoCoordinatesDTO coordinates;

	@Override
	public String getName() {
		return hubName;
	}

}
