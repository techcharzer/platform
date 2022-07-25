package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class NetworkBuyerConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.NETWORK_BUYER;
	private Double profitSharePercentageOnBooking;
}
