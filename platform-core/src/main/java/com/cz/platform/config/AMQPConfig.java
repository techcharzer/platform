package com.cz.platform.config;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableRabbit
class AMQPConfig implements RabbitListenerConfigurer {

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}

	@Bean
	public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
		factory.setMessageConverter(consumerJackson2MessageConverter());
		return factory;
	}

	@Override
	public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
	}

	@Autowired
	private RabbitMQProperties props;

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	private void createQueuesAndBindings() {
		for (Entry<String, QueueConfiguration> entry : props.getQueueConfiguration().entrySet()) {
			if (!StringUtils.equals(entry.getKey(), entry.getValue().getQueueName())) {
				String errorLog = MessageFormat.format(
						"Invalid configuration. map key name ({0}) and queue name ({1}) should be same. ",
						entry.getKey(), entry.getValue().getQueueName());
				throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), errorLog);
			}
			if (props.getQueueConsumers().contains(entry.getKey())) {
				if (BooleanUtils.isTrue(entry.getValue().getEnableDeadLetter())) {
					createQueueWithDeadLetter(entry.getKey(), entry.getValue());
				} else {
					createQueue(entry.getKey(), entry.getValue());
				}
			} else {
				log.debug(
						"queue beans creation ignored as server specific {},"
								+ " server specific queue props are missing in: {}",
						entry.getKey(), props.getQueueConsumers());
			}
		}
	}

	private Queue createQueueWithDeadLetter(QueueConfiguration queueConfig) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", queueConfig.getExchangeName());
		args.put("x-dead-letter-routing-key", queueConfig.getDelayRoutingKey());
		return new Queue(queueConfig.getQueueName(), true, false, false, args);
	}

	private Queue createWaitQueue(QueueConfiguration queueConfig) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", queueConfig.getExchangeName());
		args.put("x-dead-letter-routing-key", queueConfig.getRoutingKey());
		Integer delayTimeInSeconds = queueConfig.getDealyTimeInSeconds();
		if (ObjectUtils.isEmpty(delayTimeInSeconds)) {
			delayTimeInSeconds = 300;
		}
		delayTimeInSeconds = delayTimeInSeconds * 1000 / props.getSlashingForTesting();
		args.put("x-message-ttl", delayTimeInSeconds);
		return new Queue(queueConfig.getDelayQueueName(), true, false, false, args);
	}

	private Queue createQueue(String queueName) {
		Queue queue = new Queue(queueName);
		log.debug("queue created : {}", queue);
		return queue;
	}

	private Binding createBinding(String routingKey, TopicExchange exchange, Queue queue) {
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
		log.debug("binding created : {}", binding);
		return binding;
	}

	private TopicExchange createTopicExchange(String exchangeName) {
		TopicExchange exchange = new TopicExchange(exchangeName);
		log.debug("topic created : {}", exchange);
		return exchange;
	}

	private Queue createQueue(String key, QueueConfiguration queueConfig) {
		log.debug("creating : queue : {}", queueConfig);
		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
		TopicExchange exchange = createTopicExchange(queueConfig.getExchangeName());
		String exchangeBeanName = MessageFormat.format("{0}_exchange", key);
		beanFactory.registerSingleton(exchangeBeanName, exchange);

		Queue queue = createQueue(queueConfig.getQueueName());
		String queueBeanName = MessageFormat.format("{0}_queue", key);
		beanFactory.registerSingleton(queueBeanName, queue);

		Binding binding = createBinding(queueConfig.getRoutingKey(), exchange, queue);
		String bindingBeanName = MessageFormat.format("{0}_binding", key);
		beanFactory.registerSingleton(bindingBeanName, binding);

		return queue;
	}

	private Queue createQueueWithDeadLetter(String key, QueueConfiguration queueConfig) {
		log.debug("creating : queue : {}", queueConfig);
		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
		TopicExchange exchange = createTopicExchange(queueConfig.getExchangeName());
		String exchangeBeanName = MessageFormat.format("{0}_desitnation_exchange", key);
		beanFactory.registerSingleton(exchangeBeanName, exchange);
		Queue queue = createQueueWithDeadLetter(queueConfig);
		String queueBeanName = MessageFormat.format("{0}_desitnation_queue", key);
		beanFactory.registerSingleton(queueBeanName, queue);
		Binding mainBinding = createBinding(queueConfig.getRoutingKey(), exchange, queue);
		String bindingBeanName = MessageFormat.format("{0}_desitnation_binding", key);
		beanFactory.registerSingleton(bindingBeanName, mainBinding);

		Queue delayQueue = createWaitQueue(queueConfig);
		String delayQueueBeanName = MessageFormat.format("{0}_delay_queue", key);
		beanFactory.registerSingleton(delayQueueBeanName, delayQueue);
		Binding delayQueuBinding = createBinding(queueConfig.getDelayRoutingKey(), exchange, delayQueue);
		String delayBindingBeanName = MessageFormat.format("{0}_delay_binding", key);
		beanFactory.registerSingleton(delayBindingBeanName, delayQueuBinding);

		Queue deadLetterQueue = createQueue(queueConfig.getDeadLetterQueueName());
		String deadQueueBeanName = MessageFormat.format("{0}_dead_queue", key);
		beanFactory.registerSingleton(deadQueueBeanName, deadLetterQueue);
		Binding deadLetterBinding = createBinding(queueConfig.getDeadLetterRoutingKey(), exchange, deadLetterQueue);
		String deadBindingBeanName = MessageFormat.format("{0}_dead_binding", key);
		beanFactory.registerSingleton(deadBindingBeanName, deadLetterBinding);

		return queue;
	}

}
