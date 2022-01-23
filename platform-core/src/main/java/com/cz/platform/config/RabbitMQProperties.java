package com.cz.platform.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {
	private ConnectionProps connection;
	private Map<String, QueueConfiguration> queueConfiguration;
	private Integer slashingForTesting = 1;
	private Set<String> queueConsumers = new HashSet<>();
	private Integer retryCount = 5;
	private Integer corePoolSize = 3;
	private Integer maxPoolSize = 6;
	private Integer poolQueueCapacity = 50;

	public QueueConfiguration getQueueConfiguration(String abc) {
		if (ObjectUtils.isEmpty(queueConfiguration)) {
			return null;
		}
		return queueConfiguration.get(abc);
	}

	@Data
	public static class ConnectionProps {
		private String host;
		private Integer port;
		private String username;
		private String password;
		private String virtualHost;
	}
}
