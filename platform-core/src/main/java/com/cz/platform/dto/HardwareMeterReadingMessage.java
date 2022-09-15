package com.cz.platform.dto;

import lombok.Data;

@Data
public class HardwareMeterReadingMessage {
	private String hardwareId;
	private String socketId;
	private Long currentMeterReadingInWattHour;
	private Long electricCurrentInMilliAmpere;
	private Boolean relay;
}
