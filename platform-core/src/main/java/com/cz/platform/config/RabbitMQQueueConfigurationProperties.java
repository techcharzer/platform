package com.cz.platform.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
class RabbitMQQueueConfigurationProperties {
	private Map<String, QueueConfiguration> queueConfiguration;
	private Integer slashingForTesting = 1;
	private Integer retryCount = 5;
	
	public QueueConfiguration getQueueConfiguration(String abc) {
		return queueConfiguration.get(abc);
	}
}
