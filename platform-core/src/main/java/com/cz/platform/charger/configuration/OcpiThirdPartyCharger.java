package com.cz.platform.charger.configuration;

import lombok.Data;

@Data
public class OcpiThirdPartyCharger implements HardwareConfigurationData {
	private String uniqueId;
	
	@Override
	public String getChargerControlId() {
		return uniqueId;
	}

	@Override
	public void validate() {
		
	}
	
}
