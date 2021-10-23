package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class KiranaChargerBleConfiguration implements ChargerConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6733914490734461182L;
	private String macAddress;

	@Override
	public String getChargerControlId() {
		return macAddress;
	}
}
