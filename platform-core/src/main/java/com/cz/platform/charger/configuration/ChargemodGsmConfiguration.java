package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class ChargemodGsmConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String imeiNumber;
	private String chargemodStationId;

	@Override
	public String getChargerControlId() {
		return imeiNumber;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(imeiNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid imeiNumber");
		}
		if (ObjectUtils.isEmpty(chargemodStationId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid chargemodStationId");
		}
	}
}
