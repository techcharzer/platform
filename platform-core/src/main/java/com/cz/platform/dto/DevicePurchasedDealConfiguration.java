package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
@Deprecated
public class DevicePurchasedDealConfiguration implements DealConfigurationData, Serializable {
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -8571455224670904026L;
	private Long electricityRate;
	private String electricityBillImageUrl;
}
