package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class PremiseOwnerHostConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.PREMISE_OWNER;

	@Override
	public Double getProfitSharePercentageOnBooking() {
		return null;
	}

}
