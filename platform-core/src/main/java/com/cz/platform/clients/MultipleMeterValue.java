package com.cz.platform.clients;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.utility.CommonUtility;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultipleMeterValue {
	private Map<String, MeterValue> hardwareStatuses;

	public MeterValue get(String hardwareId, String socketId) {
		String key = CommonUtility.getKey(hardwareId, socketId);
		if (ObjectUtils.isEmpty(hardwareStatuses)) {
			return null;
		} else {
			return hardwareStatuses.get(key);
		}
	}
}
