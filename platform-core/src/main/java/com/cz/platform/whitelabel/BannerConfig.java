package com.cz.platform.whitelabel;

import java.io.Serializable;

import com.cz.platform.dto.Image;

import lombok.Data;

@Data
public class BannerConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5410675242115408684L;
	private String key;
	private Image image;
	private NavigationData navigationData;
	private Integer maxCount;
}
