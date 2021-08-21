package com.cz.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "monitor.exception.sentry")
public class SentryConfigProps {
	private String sentryDsn;
	private Boolean testSentryOnStartup = true;
}
