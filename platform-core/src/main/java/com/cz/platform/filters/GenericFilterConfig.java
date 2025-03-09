package com.cz.platform.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.config.filter")
public class GenericFilterConfig {
	private Set<String> excludedParams;
	private Map<String, Set<String>> filterToBeServed = new HashMap<>();
	private Boolean failFast = false;
}
