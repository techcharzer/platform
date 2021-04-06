package com.cz.platform.cors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;

import lombok.Data;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = PlatformConstants.CORS_CONFIG_PREFIX)
public class CorsConfigProps {
	private List<String> allowedOrigins = new ArrayList<>();
}
