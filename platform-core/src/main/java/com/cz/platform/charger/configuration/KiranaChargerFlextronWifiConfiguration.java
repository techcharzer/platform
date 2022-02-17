package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class KiranaChargerFlextronWifiConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * ccuId
	 */
	private static final long serialVersionUID = 1837645389707179L;
	private String ccuId;

	@Override
	public String getChargerControlId() {
		return ccuId;
	}
}
