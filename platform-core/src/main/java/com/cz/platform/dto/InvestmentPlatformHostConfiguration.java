package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class InvestmentPlatformHostConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.INVESTMENT_PLATFORM;
	private Double profitSharePercentageOnBooking;
}
