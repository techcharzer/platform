package com.cz.platform.dto.order;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class OrderData {
	private OrderType type;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
	@JsonSubTypes({ @Type(value = FreeInstallationOrderConfiguration.class, name = "FREE_INSTALLATION"),
			@Type(value = HardwarePurchasedOrderConfiguration.class, name = "HARDWARE_PURCHASED"),
			@Type(value = NetworkBuyersOrderConfiguration.class, name = "NETWORK_BUYERS") })
	private OrderConfiguration configuration;
}
