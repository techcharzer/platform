package com.cz.platform.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DelayQueueConfiguration {
	private String delayQueueName;
	private String destinationQueueName;
	private String exchangeName;
	private String delayRoutingKey;
	private String destinationRoutingKey;
	private Long delayInMilliseconds;
}
