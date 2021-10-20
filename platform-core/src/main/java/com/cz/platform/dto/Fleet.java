package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Fleet implements GroupConfiguration, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 36725432635689L;
	private String fleetName;
	private Boolean isVehicleNumberMandatoryForBooking;
	private String walletId;

	@Override
	public String getName() {
		return fleetName;
	}

}
