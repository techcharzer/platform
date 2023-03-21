package com.cz.platform.dto;

import java.io.Serializable;

import com.cz.platform.enums.ChargerUsageType;

import lombok.Data;

@Data
public class ProtectedChargerUsageConfiguration implements ChargerUsageTypeConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7386376707955406940L;
	private ChargerUsageType usageType = ChargerUsageType.PROTECTED;
	private String groupId;
}
