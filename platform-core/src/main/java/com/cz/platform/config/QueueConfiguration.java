package com.cz.platform.config;

import lombok.Data;

@Data
public class QueueConfiguration {
	private String queuName;
	private String routingKey;
	private String exchange;
	private Integer delay;
	private String deadLetterQueueName;
	private String deadLetterExchange;
	private String deadLetterRoutingKey;
}
