package com.cz.platform.enums;

import java.util.HashMap;
import java.util.Map;

public enum ChargerType {
	KIRANA_CHARZER_GSM, KIRANA_CHARZER_BLE, OTHER_NETWORK_CHARGER, KIRANA_CHARZER_FLEXTRON, CHARGE_MOD_BHARAT_AC,
	EV_POINT_CHARGER, KIRANA_CHARZER_FLEXTRON_WIFI, FLEXTRON_BLE_OMNI, MEKR_OMNI, MOKO_PLUG_OMNI, OCPP_16_JSON_CHARGER,
	ABB_OCPP, EXICOM_OCPP, FLEXTRON_OCPP, GO_EGO_OCPP;

	public static final Map<ChargerType, ConnectivityType> CHARGER_TYPE_TO_CONNECTIVITY = new HashMap<>();

	static {
		fillConnectivityTypeForAllChargerTypes();
	}

	private static void fillConnectivityTypeForAllChargerTypes() {
		// BLE
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_BLE, ConnectivityType.BLUETOOTH);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.FLEXTRON_BLE_OMNI, ConnectivityType.BLUETOOTH);

		// GSM MQTT
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_GSM, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_FLEXTRON, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.CHARGE_MOD_BHARAT_AC, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.EV_POINT_CHARGER, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.KIRANA_CHARZER_FLEXTRON_WIFI, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.MEKR_OMNI, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.MOKO_PLUG_OMNI, ConnectivityType.INTERNET);

		// GSM/WIFI OCPP
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.OCPP_16_JSON_CHARGER, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.ABB_OCPP, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.EXICOM_OCPP, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.FLEXTRON_OCPP, ConnectivityType.INTERNET);
		CHARGER_TYPE_TO_CONNECTIVITY.put(ChargerType.GO_EGO_OCPP, ConnectivityType.INTERNET);

	}

	public ConnectivityType getConnectivityType() {
		return getConnectivityType(this);
	}

	public static ConnectivityType getConnectivityType(ChargerType tuype) {
		return CHARGER_TYPE_TO_CONNECTIVITY.get(tuype);
	}
}
