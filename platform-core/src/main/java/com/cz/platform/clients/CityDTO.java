package com.cz.platform.clients;

import java.io.Serializable;

import lombok.Data;

@Data
public class CityDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3105294727064442647L;
	private Long cityId;
	private String cityName;
	private String state;
	private Integer sortParam;
	private Boolean isActive;
	private String urlSlug;
	private Integer groupId;
}
