package com.cz.platform.clients;

import java.time.Instant;

import org.apache.commons.lang3.BooleanUtils;

public class ChargerOnlineDTO {
	private Boolean isOnline = false;
	private Instant lastSeen;
	private Long ecInmA;
	private Long cmrInWH;

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

	public Long getEcInmA() {
		if (BooleanUtils.isTrue(isOnline)) {
			return ecInmA;
		} else {
			return 0L;
		}
	}

	public void setEcInmA(Long ecInmA) {
		this.ecInmA = ecInmA;
	}

	public Long getCmrInWH() {
		return cmrInWH;
	}

	public void setCmrInWH(Long cmrInWH) {
		this.cmrInWH = cmrInWH;
	}
}
