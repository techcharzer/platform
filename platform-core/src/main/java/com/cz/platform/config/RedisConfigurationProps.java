package com.cz.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigurationProps {
	private String host;
	private int port = 6379;
	private int db = 0;
}
