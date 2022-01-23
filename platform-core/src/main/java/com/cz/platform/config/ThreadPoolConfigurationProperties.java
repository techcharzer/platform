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
public class ThreadPoolConfigurationProperties {
	private Integer workerThreadCorePoolSize = 2;
	private Integer workerThreadMaxPoolSize = 5;
	private Integer workerThreadQueueCapacity = 500;
	private Integer schedulerThreadPoolSize = 10;
}
