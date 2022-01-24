package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class EVPointChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * ccuId mqttBrokerKey
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String deviceId;

	@Override
	public String getChargerControlId() {
		return deviceId;
	}
}
