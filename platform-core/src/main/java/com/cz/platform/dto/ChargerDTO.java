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
import com.cz.platform.enums.ChargerUsageType;
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
	private String qrCodeValue;
	private String locationId;
	private ChargerStatus status;
	private Range<Integer> openCloseTimeInSeconds;
	private List<VehicleType> supportedVehicle;
	private List<Image> images;
	private GlobalChargerHardwareInfo hardwareInfo;
	private Map<String, Long> price;
	private Boolean forceShowOnMap;
	private ChargerUsageType usageType;
	private String groupId;
	private Boolean alwaysOn;
	private AddressDTO address;
	private String operationalZoneId;
	private Set<String> viewerIds;
	private String deeplink;
	private String stickyNote;
	private Boolean sendNotificationToPremiseOwner;
	private DealConfigurationDTO dealConfiguration;
	private Instant listingTime;
	private String primaryChargePointOperatorId;
	private String secondaryChargePointOperatorId;
	private Double rating;
	private Instant updatedAt;

	public static final String[] INCLUDED_FIELDS = new String[] { "id", "name", "uniqueIdentifier", "hardwareId",
			"openCloseTimeInSeconds", "sockets", "chargerType", "supportedVehicle", "address", "images",
			"configuration", "status", "dealConfiguration", "deeplink", "usageType", "protectedNetworkId",
			"primaryChargePointOperatorId", "secondaryChargePointOperatorId", "qrCodeValue", "locationId",
			"sendNotificationToPremiseOwner", "updatedAt", "alwaysOn" };

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

	@Deprecated
	public ChargerUsageTypeConfiguration getUsageConfiguration() {
		switch (this.usageType) {
		case PUBLIC:
			return new PublicChargerUsageConfiguration();
		case PROTECTED:
			return new ProtectedChargerUsageConfiguration(ChargerUsageType.PROTECTED, this.groupId);
		case PRIVATE:
			return new PrivateChargerUsageConfiguration();
		}
		return null;
	}
}
