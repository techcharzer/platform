package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RentBasedDealConfiguration implements DealConfigurationData, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 722600999450829134L;
	private Integer rentAmount;
	private Long electricityRate;
	private String electricityBillImageUrl;
}
