package com.cz.platform.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;
import com.cz.platform.config.QueueConfiguration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.QUEUE_CONFIGURATION_KEY_PATH)
public class GenericRabbitQueueConfiguration {
	private QueueConfiguration sendNotification;
	private QueueConfiguration updateDailyTracker;
	private QueueConfiguration entityAuditing;
	private QueueConfiguration initiateOutboundCall;
	private QueueConfiguration startBookingQueueV2;
	private QueueConfiguration stopBookingQueueV2;
	private QueueConfiguration pdfGenerationAndUpload;
	private QueueConfiguration raiseBookingUtilizationAnomalyTicket;
	private QueueConfiguration httpScheduleTask;
}
