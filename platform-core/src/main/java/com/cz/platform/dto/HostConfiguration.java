package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = PremiseOwnerHostConfiguration.class, name = "PREMISE_OWNER"),
		@Type(value = NetworkBuyerConfiguration.class, name = "NETWORK_BUYER_IP"),
		@Type(value = DealerConfiguration.class, name = "DEALER"),
		@Type(value = NetworkBuyerWithWhiteLabelAppConfiguration.class, name = "NETWORK_BUYER_WITH_WHITELABEL_APP_IP"),
		@Type(value = InvestmentPlatformHostConfiguration.class, name = "INVESTMENT_PLATFORM"), })
public interface HostConfiguration {
	HostTypeEnum getType();

	Double getProfitSharePercentageOnBooking();
}
