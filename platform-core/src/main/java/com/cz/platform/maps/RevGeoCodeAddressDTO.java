package com.cz.platform.maps;

import lombok.Data;

@Data
public class RevGeoCodeAddressDTO {
	private String formatted_address;
	private String city;
	private String area;
	private String state;
	private String pincode;
}