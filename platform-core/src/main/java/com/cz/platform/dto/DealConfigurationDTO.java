package com.cz.platform.dto;

import java.io.Serializable;

import com.cz.platform.enums.DealType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class DealConfigurationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 593721389110051800L;
	private DealType dealType;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "dealType")
	@JsonSubTypes({ @Type(value = RentBasedDealConfiguration.class, name = "RENT_BASED"),
			@Type(value = ProfitShareDealConfiguration.class, name = "PROFIT_SHARE_ON_BOOKING"),
			@Type(value = DevicePurchasedDealConfiguration.class, name = "HARDWARE_PURCHASED"), })
	private DealConfigurationData configurationData;
	private ProfitShareConfiguration profitShareConfiguration;
	private Boolean immediatePayout;
}
