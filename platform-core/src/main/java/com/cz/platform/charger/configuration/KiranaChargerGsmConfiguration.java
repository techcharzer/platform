package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class KiranaChargerGsmConfiguration implements ChargerConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String deviceId;
	
	@Override
	public String getChargerControlId() {
		return deviceId;
	}
}
