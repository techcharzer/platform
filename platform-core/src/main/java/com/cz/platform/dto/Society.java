package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Society implements GroupConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3947214789228954329L;
	private String societyName;
	private String defaultImageUrl;
	private String registrationDeeplink;
	private Double serviceChargePercentage;

	@Override
	public String getName() {
		return societyName;
	}

}
