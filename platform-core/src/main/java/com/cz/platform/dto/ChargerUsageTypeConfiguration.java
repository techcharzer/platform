package com.cz.platform.dto;

import com.cz.platform.enums.ChargerUsageType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "usageType", visible = true)
@JsonSubTypes({ @Type(value = PublicChargerUsageConfiguration.class, name = "PUBLIC"),
		@Type(value = ProtectedChargerUsageConfiguration.class, name = "PROTECTED"), 
		@Type(value = PrivateChargerUsageConfiguration.class, name = "PRIVATE"),})
public interface ChargerUsageTypeConfiguration {
	ChargerUsageType getUsageType();
}
