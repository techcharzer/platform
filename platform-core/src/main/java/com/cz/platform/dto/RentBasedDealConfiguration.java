package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RentBasedDealConfiguration extends DealConfigurationData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 722600999450829134L;
	private Double electricityRates;
	private String electricityBillImageUrl;
	private Integer rentAmount;
}
