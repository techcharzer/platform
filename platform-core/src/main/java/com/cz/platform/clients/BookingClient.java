package com.cz.platform.clients;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.ActionResponse;
import com.cz.platform.dto.FailedResponseData;
import com.cz.platform.dto.IActionResponseData;
import com.cz.platform.dto.Range;
import com.cz.platform.dto.SocketDTO;
import com.cz.platform.dto.SuccessfullyCreatedDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.ErrorField;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingClient {

	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	@Autowired
	private RestTemplate template;
	@Autowired
	private SecurityConfigProps securityProps;
	@Autowired
	private UrlConfig urlConfig;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private PlatformCommonService commonService;
	@Autowired
	private CustomRabbitMQTemplate rabbitMqTemplate;
	@Autowired
	private GenericRabbitQueueConfiguration rabbitQueConfiguration;

	public BookingInfo getBookingDetails(String bookingId) {
		if (ObjectUtils.isEmpty(bookingId)) {
			return null;
		}
		log.debug("fetchig boookingDetails :{}", bookingId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/booking/{1}",
					urlConfig.getBaseUrl(), bookingId);
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<BookingInfo> response = template.exchange(url, HttpMethod.GET, entity, BookingInfo.class);
			log.info("response body : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"booking service api not working");
		}
	}

	public BookingCount getBookingCount(String userId) {
		log.info("request: {}", userId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/booking/userId/{1}",
					urlConfig.getBaseUrl(), userId);
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<BookingCount> response = template.exchange(url, HttpMethod.GET, entity, BookingCount.class);
			log.info("api response : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Booking api call failed");
		}
	}

	@Data
	public static class BookingCount {
		private Long count;
	}

	public UtilizedBookingStatistics getUtilizedBookingStatistics(String chargerId) {
		if (ObjectUtils.isEmpty(chargerId)) {
			return null;
		}
		Set<String> chargerIds = new HashSet<String>();
		chargerIds.add(chargerId);
		Map<String, UtilizedBookingStatistics> bookingStatistics = getUtilizedBookingStatistics(chargerIds);
		return bookingStatistics.get(chargerId);
	}

	public Map<String, UtilizedBookingStatistics> getUtilizedBookingStatistics(Set<String> chargerIds) {
		if (ObjectUtils.isEmpty(chargerIds)) {
			return null;
		}
		log.debug("fetchig boookingDetails :{}", chargerIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(chargerIds, headers);
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/booking/statistics/by/charger",
					urlConfig.getBaseUrl(), chargerIds);
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("response body : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UtilizedBookingStatistics>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"booking service api not working");
		}
	}

	@Data
	public static class BookingInfo {
		private String bookingId;
		private ChargerInfo chargerInfo;
		private TimingInfo timingInfo;
		private ElectricityInfo electricityInfo;
		private BookingStatus bookingStatus;
		private String bookedBy;
		private Instant createdAt;

		public Instant getStartTime() {
			if (bookingStatus.equals(BookingStatus.COMPLETED)) {
				return timingInfo.getActualDuration().getFrom();
			} else {
				return timingInfo.getBookedDuration().getFrom();
			}
		}

		public Instant getEndTime() {
			if (isBookingCompleteOrCancelled()) {
				return timingInfo.getActualDuration().getTo();
			} else {
				return timingInfo.getBookedDuration().getTo();
			}
		}

		private boolean isBookingCompleteOrCancelled() {
			return bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.CANCELLED);
		}

		public String getChargerId() {
			return chargerInfo.getChargerId();
		}
	}

	@Data
	public static class ChargerInfo {
		private String chargerId;
		private SocketDTO socket;
		private Long pricePerUnitAtBookingTime;
		private String hardwareId;
	}

	@Data
	public static class TimingInfo {
		private Range<Instant> bookedDuration;
		private Range<Instant> actualDuration;
	}

	@Data
	public static class ElectricityInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4042734682680L;
		private long electricityAlloted;
		private long startMeterReading;
		private long endMeterReading;
		private long electricityConsumed;
		private long unusedElectricity;
	}

	@Getter
	@AllArgsConstructor
	public enum BookingStatus {
		IN_PROGRESS("In Progress"), READY_TO_START("Ready To Start"), COMPLETED("Completed"), CANCELLED("Cancelled"),
		PAUSED("Paused"), UPCOMING("Upcoming"), INITIATED("Initiated"), PAYMENT_IN_PROGRESS("Payment In Progress");

		private String name;
	}

	@Data
	public static class UtilizedBookingStatistics {
		private int totalBookingCount;
		private int utilizedBookingCount;
		private Double predictionBookingWillWork;

	}

	public void startBookingAsync(IStartBookingRequest request) {
		log.info("start booking request:{}", request);
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "IInvalid request");
		}
		rabbitMqTemplate.convertAndSend(rabbitQueConfiguration.getStartBookingQueueV2(), request);
	}

	public void stopBookingAsync(IStopBookingRequest request) {
		log.info("stop booking request:{}", request);
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "IInvalid request");
		}
		rabbitMqTemplate.convertAndSend(rabbitQueConfiguration.getStopBookingQueueV2(), request);
	}

	public static interface IStartBookingRequest {
		String getSourceType();
	}

	public interface IStopBookingRequest {
		String getSourceType();
	}

	public ActionResponse startBookingSync(IStartBookingRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			return null;
		}
		log.debug("fetchig boookingDetails :{}", request);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<IStartBookingRequest> entity = new HttpEntity<>(request, headers);
		ActionResponse actionStatus = new ActionResponse();
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/booking/start",
					urlConfig.getBaseUrl());
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<SuccessfullyCreatedDTO> response = template.exchange(url, HttpMethod.POST, entity,
					SuccessfullyCreatedDTO.class);
			log.info("response body : {}", response.getBody());
			actionStatus.setSuccess(true);
			SuccessStartStopBookingResponseData successData = new SuccessStartStopBookingResponseData();
			successData.setBookingId(response.getBody().getId());
			actionStatus.setData(successData);
			return actionStatus;
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			actionStatus.setSuccess(false);
			FailedResponseData responseData = new FailedResponseData();
			try {
				ErrorField errorField = mapper.readValue(exeption.getResponseBodyAsString(), ErrorField.class);
				responseData.setReason(errorField.getMessage());
			} catch (JsonProcessingException e) {
				responseData
						.setReason("Some error occured while processing your request. Please contact customer care.");
			}
			actionStatus.setData(responseData);
		}
		return actionStatus;
	}

	public ActionResponse stopBookingSync(IStopBookingRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			return null;
		}
		log.debug("fetchig boookingDetails :{}", request);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<IStopBookingRequest> entity = new HttpEntity<>(request, headers);
		ActionResponse actionStatus = new ActionResponse();
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/booking/stop",
					urlConfig.getBaseUrl());
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<SuccessfullyCreatedDTO> response = template.exchange(url, HttpMethod.PUT, entity,
					SuccessfullyCreatedDTO.class);
			log.info("response body : {}", response.getBody());
			actionStatus.setSuccess(true);
			SuccessStartStopBookingResponseData successData = new SuccessStartStopBookingResponseData();
			successData.setBookingId(response.getBody().getId());
			actionStatus.setData(successData);
			return actionStatus;
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			actionStatus.setSuccess(false);
			FailedResponseData responseData = new FailedResponseData();
			try {
				ErrorField errorField = mapper.readValue(exeption.getResponseBodyAsString(), ErrorField.class);
				responseData.setReason(errorField.getMessage());
			} catch (JsonProcessingException e) {
				responseData
						.setReason("Some error occured while processing your request. Please contact customer care.");
			}
			actionStatus.setData(responseData);
		}
		return actionStatus;
	}

	@Data
	public static class SuccessStartStopBookingResponseData implements IActionResponseData {
		private String bookingId;
	}

}
