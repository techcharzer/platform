package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PostalAddress implements AddressData, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5926227260508066620L;
	private String postalAddress;
	private String pinCode;
}
