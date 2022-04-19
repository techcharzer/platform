package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorporateOffice implements GroupConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 337624324826783429L;
	private String officeName;
	private String registrationDeeplink;

	@Override
	public String getName() {
		return officeName;
	}

}
