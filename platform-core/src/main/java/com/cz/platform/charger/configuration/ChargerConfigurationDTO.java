package com.cz.platform.charger.configuration;

import java.io.Serializable;

import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ChargerUsageType;
import com.cz.platform.enums.ConnectivityType;

import lombok.Data;

@Data
public class ChargerConfigurationDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8410282833774514608L;
	private String id;
	private String uniqueIdentifier;
	private ChargerUsageType usageType;
	private String protectedNetworkId;
	private ChargerType chargerType;
	private ChargerConfiguration configuration;
	
	public ConnectivityType getConnectivityType() {
		return ChargerType.getConnectivityType(this.chargerType);
	}
}
