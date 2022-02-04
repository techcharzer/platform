package com.cz.platform.dto;

import java.util.List;

import com.cz.platform.enums.ChargerStatus;
import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ConnectivityType;
import com.cz.platform.enums.VehicleType;

import lombok.Data;

@Data
public class ChargerV3DTO {
	private String chargerId;
	private String name;
	private List<Image> images;
	private ChargerStatus status;
	private ChargerType chargerType;
	private Range<Integer> openCloseTimeInSeconds;
	private List<VehicleType> supportedVehicle;
	private List<SocketDTO> sockets;
	private String hardwareId;
	private String deeplink;
	private AddressDTO address;

	public static final String[] INCLUDED_FIELDS = new String[] { "id", "name", "uniqueIdentifier",
			"openCloseTimeInSeconds", "sockets", "chargerType", "supportedVehicle", "address", "images", "deeplink",
			"status", "deeplink" };

	public ConnectivityType getConnectivityType() {
		return ChargerType.getConnectivityType(this.chargerType);
	}
}
