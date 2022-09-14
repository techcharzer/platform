package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class ABBOCPPChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9180456669788679L;
	private String chargerBoxId;
	private String password;
	private OCPPVersion ocppVersion;

	@Override
	public String getChargerControlId() {
		return chargerBoxId;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(chargerBoxId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid chargerBoxId");
		}
		if (ObjectUtils.isEmpty(password)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid password");
		}
		if (ObjectUtils.isEmpty(ocppVersion)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid ocppVersion");
		}
	}
}
