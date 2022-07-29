package com.cz.platform.clients;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.utility.CommonUtility;

import lombok.Data;

@Data
public class MultipleHardwareStatus {
	private Map<String, HardwareStatusInfo> hardwareStatuses;

	public HardwareStatusInfo get(String hardwareId, String socketId) {
		String key = CommonUtility.getKey(hardwareId, socketId);
		if (ObjectUtils.isEmpty(hardwareStatuses)) {
			return null;
		} else {
			return hardwareStatuses.get(key);
		}
	}
}
