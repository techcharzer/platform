package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Hub implements GroupConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 72367225763954329L;
	private String hubName;
	private Double maxElectricityConsumptionAllowed;
	private Long pricePerUnit;

	@Override
	public String getName() {
		return hubName;
	}

}
