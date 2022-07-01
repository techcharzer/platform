package com.cz.platform.whitelabel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.dto.Image;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app.config.overlay")
public class BannerConfig {
	private String key;
	private Image image;
	private NavigationData navigationData;
	private Integer maxCount;
}
