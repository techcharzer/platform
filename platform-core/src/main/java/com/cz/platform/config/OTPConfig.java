package com.cz.platform.config;

import java.util.HashSet;
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
@ConfigurationProperties(prefix = "app.config.deeplink")
public class OTPConfig {
	private String defaultOtp = "1234";
	private Boolean useRandomOtp = true;
	private Set<String> defaultMobileNumberSet = new HashSet<>();
}
