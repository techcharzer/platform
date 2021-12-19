package com.cz.platform.dto;

import lombok.Data;

@Data
public class HardwareMeterReadingMessage {
	private String bookingId;
	private String hardwareId;
	private Long meterReading;
	private String socketId;
	private Long lastNPingSameCount;
}
