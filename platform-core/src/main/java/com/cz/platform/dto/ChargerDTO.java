package com.cz.platform.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.cz.platform.charger.configuration.GlobalChargerHardwareInfo;
import com.cz.platform.enums.ChargerStatus;
import com.cz.platform.enums.VehicleType;
import com.cz.platform.utility.CommonUtility;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

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
	private ElectricityRateInfo electricityRateInfo;
	private GlobalChargerHardwareInfo hardwareInfo;
	@Getter(value = AccessLevel.NONE)
	private Map<String, Long> price;
	private Boolean forceShowOnMap;
	private ChargerUsageTypeConfiguration usageConfiguration;
	private AddressDTO address;
	private String operationalZoneId;
	private Set<String> viewerIds;
	@Deprecated
	private DealConfigurationDTO dealConfiguration;
	private Instant listingTime;
	private String primaryChargePointOperatorId;
	private String secondaryChargePointOperatorId;

	public static final String[] INCLUDED_FIELDS = new String[] { "id", "name", "uniqueIdentifier",
			"openCloseTimeInSeconds", "sockets", "chargerType", "supportedVehicle", "address", "images", "deeplink",
			"configuration", "status", "dealConfiguration", "deeplink", "usageType", "protectedNetworkId",
			"primaryChargePointOperatorId", "secondaryChargePointOperatorId" };

	public List<SocketDTO> getSockets() {
		List<SocketDTO> sockets = new ArrayList<>();
		for (HardwareSocket hs : hardwareInfo.getSockets()) {
			SocketDTO dto = new SocketDTO();
			dto.setConnectorId(hs.getConnectorId());
			dto.setId(hs.getId());
			dto.setPower(hs.getPower());
			dto.setSocketType(hs.getSocketType());
			if (!ObjectUtils.isEmpty(price)) {
				dto.setPricePerUnit(price.get(dto.getId()));
			}
			sockets.add(dto);
		}
		return sockets;
	}

	public Image getDefaultImage() {
		return CommonUtility.getDefaultImage(images);
	}

	@Data
	public static class ElectricityRateInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3155066132243512297L;
		private Long electricityRate;
		private String reimburseToUserId;
	}

}
