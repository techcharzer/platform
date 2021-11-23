package com.cz.platform.clients;

import java.time.Instant;

import lombok.Data;

@Data
public class ChargerOnlineDTO {
	private Boolean isOnline = false;
	private Instant lastSeen;
	private Long consumptionRateInWattHour;
	private Long unitsConsumedInWattHour;
}
