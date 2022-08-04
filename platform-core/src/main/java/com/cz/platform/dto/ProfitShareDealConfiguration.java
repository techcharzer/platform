package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProfitShareDealConfiguration implements DealConfigurationData, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8917898772431810695L;
	private Long electricityRate;
	private String electricityBillImageUrl;
}
