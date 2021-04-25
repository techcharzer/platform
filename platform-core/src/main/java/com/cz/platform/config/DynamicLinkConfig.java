package com.cz.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app.config.deeplink")
public class DynamicLinkConfig {
	private String deeplinkBaseUrl;
	private String domainPrefixUrl;
	private String androidPackageName;
	private String secretKey;

}
