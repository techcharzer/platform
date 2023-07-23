package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;

import lombok.Data;

@Service
public class GenericDailyTrackerService {

	@Autowired
	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;

	@Autowired
	private CustomRabbitMQTemplate template;

	@Value("${spring.application.name}")
	private String applicationName;

	public void updateValue(Instant instant, List<Pair<TrackerKey, Long>> values) {
		DailyTrackerSaveUpdateRequest request = new DailyTrackerSaveUpdateRequest();
		request.setTime(instant);
		Map<String, Long> map = new HashMap<>();
		for (Pair<TrackerKey, Long> val : values) {
			map.put(getKey(val.getFirst()), val.getSecond());
		}
		request.setKeyValuePair(map);
		template.convertAndSend(rabbitQueueConfiguration.getUpdateDailyTracker(), request);
	}

	public void updateValue(List<Pair<TrackerKey, Long>> values) {
		DailyTrackerSaveUpdateRequest request = new DailyTrackerSaveUpdateRequest();
		request.setTime(Instant.now());
		Map<String, Long> map = new HashMap<>();
		for (Pair<TrackerKey, Long> val : values) {
			map.put(getKey(val.getFirst()), val.getSecond());
		}
		request.setKeyValuePair(map);
		template.convertAndSend(rabbitQueueConfiguration.getUpdateDailyTracker(), request);
	}

	public void incrementValue(TrackerKey key) {
		incrementValue(key, Instant.now(), 1l);
	}

	public void incrementValue(TrackerKey key, Instant instant) {
		incrementValue(key, instant, 1l);
	}

	public void incrementValue(TrackerKey key, Instant instant, Long delta) {
		DailyTrackerSaveUpdateRequest request = new DailyTrackerSaveUpdateRequest();
		request.setTime(instant);
		Map<String, Long> map = new HashMap<>();
		map.put(getKey(key), delta);
		request.setKeyValuePair(map);
		template.convertAndSend(rabbitQueueConfiguration.getUpdateDailyTracker(), request);
	}

	@Data
	static class DailyTrackerSaveUpdateRequest {
		private Instant time;
		private Map<String, Long> keyValuePair;
	}

	private String getKey(TrackerKey key) {
		if (ObjectUtils.isNotEmpty(key.getDynamicKey())) {
			return MessageFormat.format("{0}#{1}", key.getDynamicKey(), key.getKey());
		} else {
			return MessageFormat.format("{0}#{1}", applicationName.toUpperCase(), key.getKey());
		}
	}

	public static interface TrackerKey {
		/**
		 * this method is used for those cases where per charger / per user/ per booking
		 * metrics are required. 
		 * 
		 * Example find utilization on daily basis for the
		 * dashboard for ABC and xyz chargers.
		 * Here ChargerId will be the dynamic key.
		 * @return
		 */
		default String getDynamicKey() {
			return null;
		}

		/**
		 * in the above example this will be UNIT_CONSUMED
		 * @return
		 */
		String getKey();
	}

}
