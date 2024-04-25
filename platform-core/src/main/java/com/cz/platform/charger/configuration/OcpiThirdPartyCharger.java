package com.cz.platform.charger.configuration;

import lombok.Data;

@Data
public class OcpiThirdPartyCharger implements HardwareConfigurationData {
	private String evseId;
	private String uniqueId;
	
	@Override
	public String getChargerControlId() {
		return evseId;
	}

	@Override
	public void validate() {
		
	}
	
}
