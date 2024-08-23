package com.cz.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "zoho")
public class ZohoConfig {
	private String authUrl;
	private String apiUrl;
	private String refreshToken;
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}
