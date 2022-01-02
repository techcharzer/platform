package com.cz.platform.clients;

import java.time.Instant;

import org.apache.commons.lang3.BooleanUtils;

public class ChargerOnlineDTO {
	private Boolean isOnline = false;
	private Instant lastSeen;
	private Long electricCurrentInAmpere;
	private Long currentMeterReading;

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

	public Long getUnitsConsumedInWattHour() {
		return currentMeterReading;
	}

	public void setUnitsConsumedInWattHour(Long unitsConsumedInWattHour) {
		this.currentMeterReading = unitsConsumedInWattHour;
	}

	public Long getCurrentInAmpere() {
		if (BooleanUtils.isTrue(isOnline)) {
			return electricCurrentInAmpere;
		} else {
			return 0L;
		}
	}

	public void setCurrentInAmpere(Long currentInAmpere) {
		this.electricCurrentInAmpere = currentInAmpere;
	}
}
