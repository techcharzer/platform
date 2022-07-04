package com.cz.platform.whitelabel;

import com.cz.platform.dto.Image;

import lombok.Data;

@Data
public class BannerConfig {
	private String key;
	private Image image;
	private NavigationData navigationData;
	private Integer maxCount;
}
