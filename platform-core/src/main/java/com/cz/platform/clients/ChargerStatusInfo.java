package com.cz.platform.clients;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

public class ChargerStatusInfo {
	private ChargerOnlineDTO onlineDTO;
	private Boolean relay;
	private Long ecInmA;
	private Long cmrInWH;

	public Long getEcInmA() {
		if (!ObjectUtils.isEmpty(onlineDTO) && BooleanUtils.isTrue(onlineDTO.getIsOnline())) {
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

	public Boolean getRelay() {
		return relay;
	}

	public void setRelay(Boolean relay) {
		this.relay = relay;
	}
}
