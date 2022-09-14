package com.cz.platform.charger.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class KiranaChargerFlextronConfiguration implements HardwareConfigurationData, Serializable {

	/**
	 * ccuId mqttBrokerKey
	 */
	private static final long serialVersionUID = 1839211805479707179L;
	private String ccuId;
	private String bleMacAddress;
	private String mqttBrokerKey;

	@Override
	public String getChargerControlId() {
		return ccuId;
	}

	@Override
	public void validate() {
		if (ObjectUtils.isEmpty(ccuId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid ccuId");
		}
		if (ObjectUtils.isEmpty(bleMacAddress)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid bleMacAddress");
		}
		if (ObjectUtils.isEmpty(mqttBrokerKey)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid mqttBrokerKey");
		}
	}
}
