package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class DevicePurchasedDealConfiguration extends DealConfigurationData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8571455224670904026L;
	private Long electricityRates;
	private String electricityBillImageUrl;
}
