package org.charzer.platform.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.cors.config")
public class CorsConfigProps {
	private boolean disabled;
}
