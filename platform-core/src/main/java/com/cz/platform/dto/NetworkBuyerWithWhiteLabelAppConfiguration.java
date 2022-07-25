package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;

import lombok.Data;

@Data
public class NetworkBuyerWithWhiteLabelAppConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.NETWORK_BUYER_WITH_WHITELABEL_APP;
	private Double profitSharePercentageOnBooking;
}
