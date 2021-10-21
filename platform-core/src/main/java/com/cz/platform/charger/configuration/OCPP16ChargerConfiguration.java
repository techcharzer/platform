package com.cz.platform.charger.configuration;

import java.io.Serializable;

import lombok.Data;

@Data
public class OCPP16ChargerConfiguration implements ChargerConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9180953709391315499L;
	private String chargerBoxId;
	private OCPPCustomistionType ocppCustomisationType;

	@Override
	public String getChargerControlId() {
		return chargerBoxId;
	}
}
