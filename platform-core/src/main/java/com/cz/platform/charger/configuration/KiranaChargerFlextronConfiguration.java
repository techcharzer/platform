package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class KiranaChargerFlextronConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String ccuId;
	private String serverKey;

	@Override
	public String getChargerControlId() {
		return ccuId;
	}
}
