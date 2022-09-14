package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class MokoPlugOmniConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3370338861660413796L;
	private String imeiNumber;
	private String bleMacAddress;

	@Override
	public String getChargerControlId() {
		return imeiNumber;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(imeiNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid imeiNumber");
		}
		if (ObjectUtils.isEmpty(bleMacAddress)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid bleMacAddress");
		}
	}
}
