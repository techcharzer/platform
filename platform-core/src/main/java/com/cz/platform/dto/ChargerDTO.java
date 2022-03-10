package com.cz.platform.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.cz.platform.charger.configuration.GlobalChargerHardwareInfo;
import com.cz.platform.enums.ChargerStatus;
import com.cz.platform.enums.VehicleType;
import com.cz.platform.utility.CommonUtility;

import lombok.Data;

@Data
public class ChargerDTO {
	private String chargerId;
	private String name;
	private ChargerStatus status;
	private Range<Integer> openCloseTimeInSeconds;
	private List<VehicleType> supportedVehicle;
	private List<Image> images;
	private GlobalChargerHardwareInfo hardwareInfo;
	private Map<String, Long> price;
	private ChargerUsageTypeConfiguration usageConfiguration;
	private AddressDTO address;
	private DealConfigurationDTO dealConfiguration;

	public static final String[] INCLUDED_FIELDS = new String[] { "id", "name", "uniqueIdentifier",
			"openCloseTimeInSeconds", "sockets", "chargerType", "supportedVehicle", "address", "images", "deeplink",
			"configuration", "status", "dealConfiguration", "deeplink", "usageType", "protectedNetworkId" };

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
}
