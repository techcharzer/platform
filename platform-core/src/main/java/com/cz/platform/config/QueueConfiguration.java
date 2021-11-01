package com.cz.platform.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueueConfiguration {
	private String queueName;
	private String exchangeName;
	private String routingKey;
}
