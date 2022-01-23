package com.cz.platform.config;

import java.text.MessageFormat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueueConfiguration {
	private String queueName;
	private String exchangeName;
	private String routingKey;
	private Boolean enableDeadLetter;
	private Integer dealyTimeInSeconds;

	public String getDelayQueueName() {
		return MessageFormat.format("{0}_delay_queue", this.getQueueName());
	}

	public String getDelayRoutingKey() {
		return MessageFormat.format("{0}_delay_routing", this.getRoutingKey());
	}

	public String getDeadLetterQueueName() {
		return MessageFormat.format("{0}_dead_queue", this.getQueueName());
	}

	public String getDeadLetterRoutingKey() {
		return MessageFormat.format("{0}_dead_routing", this.getRoutingKey());
	}
}
