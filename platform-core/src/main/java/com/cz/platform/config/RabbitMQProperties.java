package com.cz.platform.config;

import java.util.HashMap;
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
	private Map<String, QueueConfiguration> queueConfiguration = new HashMap<>();
	private Integer slashingForTesting = 1;
	private Set<String> queueConsumers = new HashSet<>();
	private Integer retryCount = 5;
	private Integer corePoolSize = 3;
	private Integer maxPoolSize = 6;
	private Integer poolQueueCapacity = 50;

	private static final Map<String, QueueConfiguration> QUEUE_CONFIG_MAP = new HashMap<>();

	public Map<String, QueueConfiguration> getQueuConfig() {
		if (ObjectUtils.isEmpty(QUEUE_CONFIG_MAP)) {
			for (QueueConfiguration config : queueConfiguration.values()) {
				QUEUE_CONFIG_MAP.put(config.getQueueName(), config);
			}
		}
		return QUEUE_CONFIG_MAP;
	}

	public QueueConfiguration getQueueConfig(String queueName) {
		return getQueuConfig().get(queueName);
	}
}
