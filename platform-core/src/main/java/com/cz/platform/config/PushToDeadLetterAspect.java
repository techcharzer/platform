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
		boolean countExceeded = exceededRetryCount(message.getMessageProperties().getXDeathHeader(),
				props.getRetryCount());
		log.debug("retry count exceeded: {}", countExceeded);
		if (countExceeded) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			PushToDeadLetterQueue annotations = method.getAnnotation(PushToDeadLetterQueue.class);
			String queueName = environment.resolvePlaceholders(annotations.queueName());
			log.debug("queu name: {} in data : {}", queueName, props.getQueueConfiguration());
			QueueConfiguration qConfig = props.getQueueConfig(queueName);
			String data = new String(message.getBody());
			log.error("pushing the message to dead letter queue : {}", data, qConfig.getDeadLetterQueueName());
			rabbitTemplate.send(qConfig.getExchangeName(), qConfig.getDeadLetterRoutingKey(), message);
		} else {
			joinPoint.proceed();
		}
	}

	public static boolean exceededRetryCount(List<Map<String, ?>> xDeath, Integer maxRetryCount) {
		if (xDeath != null && xDeath.size() >= 1) {
			Long count = (Long) xDeath.get(0).get("count");
			return count >= maxRetryCount;
		}
		return false;
	}

}