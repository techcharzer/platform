package com.cz.platform.utility.filters;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app.config.filter")
public class FilterConfig {
	private Set<String> allowedFilters;
	private Set<String> excludedParams;
	private Boolean failFast = false;
}
