package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class AddressDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3297786397246929480L;
	private GeoCoordinatesDTO coordinates;
	private String address;
	private Long cityId;
	private String pinCode;
}
