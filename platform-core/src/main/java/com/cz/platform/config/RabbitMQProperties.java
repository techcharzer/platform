package com.cz.platform.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private List<QueueConfiguration> queueConfiguration;
	private Integer slashingForTesting = 1;
	private Set<String> queueConsumers = new HashSet<>();
	private Integer retryCount = 5;
	private Integer corePoolSize = 3;
	private Integer maxPoolSize = 6;
	private Integer poolQueueCapacity = 50;

	private static Map<String, QueueConfiguration> QUEUE_NAME_MAP;

	public Map<String, QueueConfiguration> getQueueNameConfigMap() {
		if (ObjectUtils.isEmpty(QUEUE_NAME_MAP)) {
			Map<String, QueueConfiguration> internal = new HashMap<>();
			if (!ObjectUtils.isEmpty(queueConfiguration)) {
				for (QueueConfiguration config : queueConfiguration) {
					internal.put(config.getQueueName(), config);
				}
			}
			QUEUE_NAME_MAP = Collections.unmodifiableMap(internal);
		}
		return QUEUE_NAME_MAP;

	}

}
