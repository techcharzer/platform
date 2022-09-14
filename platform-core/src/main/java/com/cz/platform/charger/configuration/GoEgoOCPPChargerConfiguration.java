package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class GoEgoOCPPChargerConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8132764898734835499L;
	private String chargerBoxId;
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
		if (ObjectUtils.isEmpty(ocppVersion)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid ocppVersion");
		}
	}
}
