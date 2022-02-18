package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class HybridAddressDTO implements Serializable, AddressData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3297786397246929480L;
	private GeoCoordinatesDTO coordinates;
	private String postalAddress;
	private String cityId;
	private String pinCode;
}
