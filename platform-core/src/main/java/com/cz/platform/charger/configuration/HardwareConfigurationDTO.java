package com.cz.platform.charger.configuration;

import java.io.Serializable;

import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ConnectivityType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class HardwareConfigurationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8410282833774514608L;
	private ChargerType chargerType;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "chargerType")
	@JsonSubTypes({ @Type(value = OCPP16ChargerConfiguration.class, name = "OCPP_16_JSON_CHARGER"),
			@Type(value = KiranaChargerBleConfiguration.class, name = "KIRANA_CHARZER_BLE"),
			@Type(value = KiranaChargerFlextronConfiguration.class, name = "KIRANA_CHARZER_FLEXTRON"),
			@Type(value = KiranaChargerFlextronWifiConfiguration.class, name = "KIRANA_CHARZER_FLEXTRON_WIFI"),
			@Type(value = KiranaChargerGsmConfiguration.class, name = "KIRANA_CHARZER_GSM"),
			@Type(value = ChargemodGsmConfiguration.class, name = "CHARGE_MOD_BHARAT_AC"),
			@Type(value = EVPointChargerConfiguration.class, name = "EV_POINT_CHARGER"),
			@Type(value = MekrOmniConfiguration.class, name = "MEKR_OMNI"),
			@Type(value = MokoPlugOmniConfiguration.class, name = "MOKO_PLUG_OMNI"), })
	private HardwareConfigurationData configuration;

	public ConnectivityType getConnectivityType() {
		return ChargerType.getConnectivityType(this.chargerType);
	}
}
