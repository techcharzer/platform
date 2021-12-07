package com.cz.platform.clients;

import java.time.Instant;

import org.apache.commons.lang3.BooleanUtils;

public class ChargerOnlineDTO {
	private Boolean isOnline = false;
	private Instant lastSeen;
	private Long consumptionRateInWattHour;
	private Long unitsConsumedInWattHour;

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Instant getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Instant lastSeen) {
		this.lastSeen = lastSeen;
	}

	public Long getConsumptionRateInWattHour() {
		if (BooleanUtils.isTrue(isOnline)) {
			return consumptionRateInWattHour;
		} else {
			return 0L;
		}
	}

	public void setConsumptionRateInWattHour(Long consumptionRateInWattHour) {
		this.consumptionRateInWattHour = consumptionRateInWattHour;
	}

	public Long getUnitsConsumedInWattHour() {
		return unitsConsumedInWattHour;
	}

	public void setUnitsConsumedInWattHour(Long unitsConsumedInWattHour) {
		this.unitsConsumedInWattHour = unitsConsumedInWattHour;
	}
}
