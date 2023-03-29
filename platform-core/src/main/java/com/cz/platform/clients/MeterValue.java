package com.cz.platform.clients;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import lombok.Data;

@Data
public class MeterValue {
	private String hardwareId;
	private String socketId;
	private HardwareStatusDTO onlineDTO;
	private Boolean relay;
	private Double stateOfCharge;
	private Long electricCurrentInMilliAmpere;
	private Long currentMeterReadingInWattHour;
	private String connectorStatus;

	public Long getElectricCurrentInMilliAmpere() {
		if (!ObjectUtils.isEmpty(onlineDTO) && BooleanUtils.isTrue(onlineDTO.getIsOnline())) {
			return electricCurrentInMilliAmpere;
		} else {
			return 0L;
		}
	}
}
