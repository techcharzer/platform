package com.cz.platform.cors;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "app.cors.config")
public class CorsConfigProps {
	private String allowedOrigin;
	private List<String> allowedOrigins;
}
