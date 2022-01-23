package com.cz.platform.utility;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.cz.platform.config.QueueConfiguration;
import com.cz.platform.config.RabbitMQProperties;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CustomRabbitMQTemplate {
	private RabbitMQProperties props;
	private RabbitTemplate template;

	public void convertAndSend(String queueName, Object data) {
		log.debug("queueName: {}", queueName);
		QueueConfiguration q = props.getQueueNameConfigMap().get(queueName);
		if (ObjectUtils.isEmpty(q)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid queue configuration");
		}
		log.info("messgae {} queued to: {}", data, q);
		template.convertAndSend(q.getExchangeName(), q.getRoutingKey(), data);
	}
}
