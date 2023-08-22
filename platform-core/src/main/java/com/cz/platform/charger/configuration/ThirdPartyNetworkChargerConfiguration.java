package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class ThirdPartyNetworkChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9180953709391315499L;
	private String dataSource;

	@Override
	public String getChargerControlId() {
		return dataSource;
	}

	@Override
	public void validate() {

	}
}