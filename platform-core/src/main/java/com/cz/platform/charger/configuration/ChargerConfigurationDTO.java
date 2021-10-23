package com.cz.platform.charger.configuration;

import java.io.Serializable;

import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ChargerUsageType;
import com.cz.platform.enums.ConnectivityType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import lombok.Data;

@Data
public class ChargerConfigurationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8410282833774514608L;
	private String id;
	private String uniqueIdentifier;
	private ChargerUsageType usageType;
	private String protectedNetworkId;
	private ChargerType chargerType;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "chargerType")
	@JsonSubTypes({ @Type(value = OCPP16ChargerConfiguration.class, name = "OCPP_16_JSON_CHARGER"),
			@Type(value = KiranaChargerBleConfiguration.class, name = "KIRANA_CHARZER_BLE"),
			@Type(value = KiranaChargerFlextronConfiguration.class, name = "KIRANA_CHARZER_FLEXTRON"),
			@Type(value = KiranaChargerGsmConfiguration.class, name = "KIRANA_CHARZER_GSM") })
	private ChargerConfiguration configuration;

	public ConnectivityType getConnectivityType() {
		return ChargerType.getConnectivityType(this.chargerType);
	}
}
