package com.cz.platform.clients;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

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
import com.cz.platform.dto.Range;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class BookingClient {

	private RestTemplate template;
	private SecurityConfigProps securityProps;
	private UrlConfig urlConfig;
	private ObjectMapper mapper;
	private PlatformCommonService commonService;

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
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<BookingInfo> response = template.exchange(url, HttpMethod.GET, entity, BookingInfo.class);
			log.info("response body : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (commonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"booking service api not working");
		}
	}

	public Map<String, UtilizedBookingStatistics> getUtilizedBookingStatistics(Set<String> bookingId) {
		if (ObjectUtils.isEmpty(bookingId)) {
			return null;
		}
		log.debug("fetchig boookingDetails :{}", bookingId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(bookingId, headers);
		try {
			String url = MessageFormat.format(
					"{0}/booking-service/secure/internal-call/booking/statistics/by/charger",
					urlConfig.getBaseUrl(), bookingId);
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("response body : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UtilizedBookingStatistics>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (commonService.handle404Error(exeption.getResponseBodyAsString())) {
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
		private String socketId;
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

}
