package com.cz.platform.clients;

import lombok.Data;

@Data
public class CityDTO {
	private Long cityId;
	private String cityName;
	private Integer sortParam;
	private Boolean isActive;
	private Integer groupId;
}
