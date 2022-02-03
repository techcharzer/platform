package com.cz.platform.dto;

import com.cz.platform.enums.ChargerType;

import lombok.Data;

@Data
public class HardwareMeterReadingMessage {
	private String bookingId;
	private String hardwareId;
	private ChargerType chargerType;
	private Long cmrInWH;
	private String socketId;
	private Long lastNPingSameCount;
	private Long ecInmA;
}
