package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.clients.UrlConfig;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;
import com.cz.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
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

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UrlConfig urlConfig;

	@Autowired
	private SecurityConfigProps securityProps;

	@Autowired
	private ObjectMapper mapper;

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
		if (key instanceof ItemTrackerKey) {
			return key.getKey();
		}
		return MessageFormat.format("{0}#{1}", applicationName.toUpperCase(), key.getKey());
	}

	public static interface TrackerKey {
		String getKey();
	}

	@Data
	@AllArgsConstructor
	public static class ItemTrackerKey implements TrackerKey {
		private String itemId;
		private String track;

		@Override
		public String getKey() {
			return MessageFormat.format("{0}#{1}", itemId, track);
		}
	}

	public List<TrackerRequestResponseForSingleDay> getTrackerForToday(List<ItemTrackerKey> keys) {
		List<TrackerRequestResponseForMultipleDays> response = getTrackerForLastNDays(1, keys);
		List<TrackerRequestResponseForSingleDay> days = new ArrayList<>();
		for (TrackerRequestResponseForMultipleDays multipleDaysResponse : response) {
			TrackerRequestResponseForSingleDay singleDay = new TrackerRequestResponseForSingleDay();
			singleDay.setRequest(multipleDaysResponse.getRequest());
			singleDay.setFormattedKey(multipleDaysResponse.getFormattedKey());
			singleDay.setResponse(multipleDaysResponse.getResponse().get(0));
			days.add(singleDay);
		}
		return days;
	}

	@Data
	public static class TrackerRequestResponseForSingleDay {
		private TrackerKey request;
		private String formattedKey;
		private TrackerResponse response;
	}

	public List<TrackerRequestResponseForMultipleDays> getTrackerForLastNDays(int noOfLasDays,
			List<ItemTrackerKey> keys) {
		List<String> dates = getDates(noOfLasDays);
		if (ObjectUtils.isEmpty(dates)) {
			return Collections.emptyList();
		}
		GetTrackingRequest request = new GetTrackingRequest();
		request.setDates(dates);
		Set<String> keysFormatted = new HashSet<String>();
		List<TrackerRequestResponseForMultipleDays> requestResponses = new ArrayList<>();
		for (TrackerKey key : keys) {
			String formattedKey = key.getKey();
			keysFormatted.add(formattedKey);
			TrackerRequestResponseForMultipleDays requestResponse = new TrackerRequestResponseForMultipleDays();
			requestResponse.setFormattedKey(formattedKey);
			requestResponse.setRequest(key);
			requestResponses.add(requestResponse);
		}
		request.setKeys(keysFormatted);
		log.debug("fetching tracker: {}", request);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("lms-service"));
		HttpEntity<GetTrackingRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = MessageFormat.format("{0}/lms/secure/internal-call/tracker", urlConfig.getBaseUrl());
			log.debug("request details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			Map<String, Map<String, Long>> dateKeyCountMap = mapper.convertValue(response.getBody(),
					new TypeReference<Map<String, Map<String, Long>>>() {
					});
			for (TrackerRequestResponseForMultipleDays requestResponse : requestResponses) {
				List<TrackerResponse> responses = new ArrayList<>();
				for (String date : dates) {
					TrackerResponse trackResponse = new TrackerResponse();
					long count = dateKeyCountMap.get(date).getOrDefault(requestResponse.getFormattedKey(), 0L);
					trackResponse.setCount(count);
					trackResponse.setDate(date);
					responses.add(trackResponse);
				}
				requestResponse.setResponse(responses);
			}
			log.info("response: {}", requestResponses);
			return requestResponses;
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"tracker service not working");
		}

	}

	@Data
	public static class TrackerRequestResponseForMultipleDays {
		private TrackerKey request;
		@JsonIgnore
		private String formattedKey;
		private List<TrackerResponse> response;
	}

	@Data
	public static class TrackerResponse {
		private String date;
		private Long count;
	}

	public static List<String> getDates(int noOfLasDays) {
		if (noOfLasDays < 1) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid number of days");
		}
		List<String> dates = new ArrayList<String>();
		Instant instant = Instant.now();
		for (int i = 0; i < noOfLasDays; ++i) {
			Instant exactDateTime = instant.minusSeconds(i * 24 * 3600);
			dates.add(CommonUtility.getDate(exactDateTime));
		}
		log.debug("dates: {}", dates);
		return dates;
	}

	@Data
	public static class GetTrackingRequest {
		private List<String> dates;
		private Set<String> keys;
	}

}
