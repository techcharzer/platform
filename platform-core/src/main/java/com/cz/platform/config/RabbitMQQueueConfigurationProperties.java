package com.cz.platform.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
class RabbitMQQueueConfigurationProperties {
	private Map<String, QueueConfiguration> queueConfiguration;
	private Integer slashingForTesting = 1;
	private Set<String> queueConsumers = new HashSet<>();
	private Integer retryCount = 5;
	
	public QueueConfiguration getQueueConfiguration(String abc) {
		return queueConfiguration.get(abc);
	}
}
