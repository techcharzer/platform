package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.clients.UrlConfig;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GenericHttpAPIScheduler {

	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;
	private CustomRabbitMQTemplate template;
	private UrlConfig urlConfig;

	@Data
	public static class HTTPScheduleTask {
		private String url;
		private String taskType;
		private String name;
		private HttpMethod method;
		private Long scheduleTime;
	}

	public void scheduleAutomaticAnalysis(String relativeUrl, HttpMethod httpMethod, String uniqueId, String type,
			Instant time) {
		HTTPScheduleTask task = new HTTPScheduleTask();
		String url = MessageFormat.format("{0}{1}", urlConfig.getBaseUrl(), relativeUrl);
		task.setUrl(url);
		task.setMethod(httpMethod);
		task.setName(uniqueId);
		task.setTaskType(type);
		task.setScheduleTime(time.getEpochSecond());
	}

	public void scheduleAutomaticAnalysis(HTTPScheduleTask task) {
		template.convertAndSend(rabbitQueueConfiguration.getHttpScheduleTask(), task);
	}
}
