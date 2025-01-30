package com.cz.platform.dto;

import java.io.Serializable;

import com.cz.platform.enums.ChargerUsageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtectedChargerUsageConfiguration implements ChargerUsageTypeConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7386376707955406940L;
	private ChargerUsageType usageType = ChargerUsageType.PROTECTED;
	private String groupId;
}
