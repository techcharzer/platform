package com.cz.platform.clients;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import lombok.Data;

@Data
public class CurrentChargerStatusInfo {
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

}
