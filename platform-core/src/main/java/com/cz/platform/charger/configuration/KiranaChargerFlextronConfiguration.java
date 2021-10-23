package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class KiranaChargerFlextronConfiguration implements ChargerConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String ccuId;

	@Override
	public String getChargerControlId() {
		return ccuId;
	}
}
