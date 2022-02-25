package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenericDailyTrackerService {

	@Autowired
	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;

	@Autowired
	private CustomRabbitMQTemplate template;

	@Value("${spring.application.name}")
	private String applicationName;

	public void incrementValue(String key) {
		incrementValue(key, Instant.now());
	}

	public void updateValue(String key, Long value) {
		DailyTrackerSaveUpdateRequest request = new DailyTrackerSaveUpdateRequest();
		request.setTime(Instant.now());
		Map<String, Long> map = new HashMap<>();
		map.put(getKey(key), value);
		request.setKeyValuePair(map);
		template.convertAndSend(rabbitQueueConfiguration.getUpdateDailyTracker(), request);
	}

	public void incrementValue(String key, Instant instant) {
		DailyTrackerSaveUpdateRequest request = new DailyTrackerSaveUpdateRequest();
		request.setTime(instant);
		Map<String, Long> map = new HashMap<>();
		map.put(getKey(key), 1L);
		request.setKeyValuePair(map);
		template.convertAndSend(rabbitQueueConfiguration.getUpdateDailyTracker(), request);
	}

	@Data
	static class DailyTrackerSaveUpdateRequest {
		private Instant time;
		private Map<String, Long> keyValuePair;
	}

	private String getKey(String key) {
		return MessageFormat.format("{0}#{1}", applicationName.toUpperCase(), key.toUpperCase());
	}

}
