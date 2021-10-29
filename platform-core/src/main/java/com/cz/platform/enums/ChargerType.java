package com.cz.platform.enums;

import java.util.HashMap;
import java.util.Map;

public enum ChargerType {
	KIRANA_CHARZER_GSM, KIRANA_CHARZER_BLE, OTHER_NETWORK_CHARGER, OCPP_16_JSON_CHARGER, KIRANA_CHARZER_FLEXTRON, CHARGE_MOD_BHARAT_AC;

	public static final Map<ChargerType, ConnectivityType> CHARGER_TYPE_TO_CONNECTIVITY = new HashMap<>();

	static {
		fillConnectivityTypeForAllChargerTypes();
	}

	private static void fillConnectivityTypeForAllChargerTypes() {
		// BLE
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_BLE, ConnectivityType.BLUETOOTH);

		// GSM
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.OCPP_16_JSON_CHARGER, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_GSM, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_FLEXTRON, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.CHARGE_MOD_BHARAT_AC, ConnectivityType.INTERNET);
	}

	public ConnectivityType getConnectivityType() {
		return getConnectivityType(this);
	}

	public static ConnectivityType getConnectivityType(ChargerType tuype) {
		return CHARGER_TYPE_TO_CONNECTIVITY.get(tuype);
	}
}
