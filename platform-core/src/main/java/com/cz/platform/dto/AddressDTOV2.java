package com.cz.platform.dto;

import java.io.Serializable;

import com.cz.platform.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class AddressDTOV2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3297786397246929480L;
	private AddressType type;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
	@JsonSubTypes({ @Type(value = GeoCoordinatesDTO.class, name = "GPS_COORDINATES"),
			@Type(value = PostalAddress.class, name = "POSTAL_ADDRESS"),
			@Type(value = HybridAddressDTO.class, name = "HYBRID_ADDRESS"),})
	private Address address;
}
