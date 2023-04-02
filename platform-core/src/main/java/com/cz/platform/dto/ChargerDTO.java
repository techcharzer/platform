package com.cz.platform.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import com.cz.platform.charger.configuration.GlobalChargerHardwareInfo;
import com.cz.platform.enums.ChargerStatus;
import com.cz.platform.enums.VehicleType;
import com.cz.platform.utility.CommonUtility;

import lombok.Data;

@Data
public class ChargerDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7652796832659881532L;
	private String chargerId;
	private String name;
	private ChargerStatus status;
	private Range<Integer> openCloseTimeInSeconds;
	private List<VehicleType> supportedVehicle;
	private List<Image> images;
	private GlobalChargerHardwareInfo hardwareInfo;
	private Boolean forceShowOnMap;
	private ElectricityRateInfo electricityRateInfo;
	private ChargerUsageTypeConfiguration usageConfiguration;
	private AddressDTO address;
	private String operationalZoneId;
	private Set<String> viewerIds;
	private Instant listingTime;
	private String primaryChargePointOperatorId;
	private String secondaryChargePointOperatorId;


	public Image getDefaultImage() {
		return CommonUtility.getDefaultImage(images);
	}

	@Data
	public static class ElectricityRateInfo {
		private Long electricityRate;
		private String reimburseToUserId;
	}

}
