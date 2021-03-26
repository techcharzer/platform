package com.cz.platform.custom.events;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "cz.config.custom.events")
@RefreshScope
public class CustomEventConfig {
	private Map<Integer, String> listOfCustomeEvents = new HashMap<>();
}
