package com.cz.platform.enums;

import java.util.HashMap;
import java.util.Map;

public enum ChargerType {
	KIRANA_CHARZER_GSM, KIRANA_CHARZER_BLE, OTHER_NETWORK_CHARGER, OCPP_16_JSON_CHARGER;

	public static final Map<ChargerType, ConnectivityType> CHARGER_TYPE_TO_CONNECTIVITY = new HashMap<>();

	static {
		fillConnectivityTypeForAllChargerTypes();
	}

	private static void fillConnectivityTypeForAllChargerTypes() {
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.OCPP_16_JSON_CHARGER, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_GSM, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_BLE, ConnectivityType.BLUETOOTH);
	}

	public ConnectivityType getConnectivityType() {
		return getConnectivityType(this);
	}

	private static ConnectivityType getConnectivityType(ChargerType tuype) {
		return CHARGER_TYPE_TO_CONNECTIVITY.get(tuype);
	}
}
