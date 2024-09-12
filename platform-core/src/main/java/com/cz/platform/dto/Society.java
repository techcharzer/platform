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
	private SocietyConfiguration configuration;

	@Override
	public String getName() {
		return societyName;
	}

	@Data
	public static class SocietyConfiguration implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3173206499176485600L;
		private UtilizationLimit utilizationLimit;
	}

	@Data
	public static class UtilizationLimit implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7200456165935601236L;
		private Boolean isApplied;
		private Long valueInWattHour;
	}

}
