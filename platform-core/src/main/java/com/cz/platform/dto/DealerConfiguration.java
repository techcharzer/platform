package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class DealerConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.DEALER;
	private Double profitSharePercentageOnBooking;
}
