package com.cz.platform.charger.configuration;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class OCPP16ChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9180953709391315499L;
	private String chargerBoxId;
	private OCPPVersion version;
	private OCPPServer server;
	private Map<String, String> customData;

	@Override
	public String getChargerControlId() {
		return chargerBoxId;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(chargerBoxId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid chargerBoxId");
		}
	}

	@Getter
	@AllArgsConstructor
	public enum OCPPVersion {
		JSON_16("1.6J");

		private String versionName;
	}
	
	public enum OCPPServer {
		STEVE, OCPP_V1
	}

}
