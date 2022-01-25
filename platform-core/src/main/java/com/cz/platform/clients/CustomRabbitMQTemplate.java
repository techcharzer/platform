package com.cz.platform.clients;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.cz.platform.config.QueueConfiguration;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CustomRabbitMQTemplate {
	private RabbitTemplate template;

	public void convertAndSend(QueueConfiguration qConfig, Object data) {
		if (ObjectUtils.isEmpty(qConfig)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid queue configuration");
		}
		if (ObjectUtils.isEmpty(qConfig.getRoutingKey())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid queue routingKey");
		}
		if (ObjectUtils.isEmpty(qConfig.getExchangeName())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid queue exchangeName");
		}
		log.info("messgae {} queued to: {}", data, qConfig);
		template.convertAndSend(qConfig.getExchangeName(), qConfig.getRoutingKey(), data);
	}
}
