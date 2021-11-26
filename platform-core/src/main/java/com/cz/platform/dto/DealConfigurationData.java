package com.cz.platform.dto;

import lombok.Data;

@Data
public abstract class DealConfigurationData {
	protected String userId;
	private Long electricityRate;
	private String electricityBillImageUrl;
}
