package com.cz.platform.dto;

import java.io.Serializable;

import com.cz.platform.enums.ChargerUsageType;

import lombok.Data;

@Data
public class PrivateChargerUsageConfiguration implements ChargerUsageTypeConfiguration, Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8621268082263284456L;
	private ChargerUsageType usageType = ChargerUsageType.PRIVATE;

}
