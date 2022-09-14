package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class KiranaChargerBleConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6733914490734461182L;
	private String macAddress;

	@Override
	public String getChargerControlId() {
		return macAddress;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(macAddress)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid macAddress");
		}
	}
}
