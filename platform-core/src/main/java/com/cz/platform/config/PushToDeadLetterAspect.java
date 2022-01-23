package com.cz.platform.config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class PushToDeadLetterAspect {

	@Autowired
	private RabbitMQProperties props;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private Environment environment;

	@Around("@annotation(PushToDeadLetterQueue)")
	public void trace(ProceedingJoinPoint joinPoint) throws Throwable {
		Message message = (Message) joinPoint.getArgs()[0];
		int count = getRetryCount(message.getMessageProperties().getXDeathHeader());
		log.debug("current count: {}, retry count", count, props.getRetryCount());
		if (count >= props.getRetryCount()) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			PushToDeadLetterQueue annotations = method.getAnnotation(PushToDeadLetterQueue.class);
			String queueName = environment.resolvePlaceholders(annotations.queueName());
			QueueConfiguration qConfig = props.getQueueConfig(queueName);
			log.debug("queue name: {} and its config : {}", queueName, qConfig);
			String data = new String(message.getBody());
			log.error("pushing the message: {} to dead letter queue: {}", data, qConfig.getDeadLetterQueueName());
			rabbitTemplate.send(qConfig.getExchangeName(), qConfig.getDeadLetterRoutingKey(), message);
		} else {
			joinPoint.proceed();
		}
	}

	public int getRetryCount(List<Map<String, ?>> xDeath) {
		if (xDeath != null && xDeath.size() >= 1) {
			return (int) xDeath.get(0).get("count");
		}
		return 0;
	}

}