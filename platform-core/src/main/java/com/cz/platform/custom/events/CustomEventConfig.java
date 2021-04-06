package com.cz.platform.custom.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.CUSTOM_EVENT_CONFIG_PREFIX)
@RefreshScope
public class CustomEventConfig {
	private List<String> listOfCustomeEvents = new ArrayList<>();
}
