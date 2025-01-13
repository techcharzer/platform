package com.cz.platform.clients;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.ActionResponse;
import com.cz.platform.dto.AddressDTO;
import com.cz.platform.dto.DealConfigurationDTO;
import com.cz.platform.dto.FailedResponseData;
import com.cz.platform.dto.HardwareSocket;
import com.cz.platform.dto.IActionResponseData;
import com.cz.platform.dto.Range;
import com.cz.platform.dto.SocketDTO;
import com.cz.platform.dto.SuccessfullyCreatedDTO;
import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ChargerUsageType;
import com.cz.platform.enums.VehicleType;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.ErrorField;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
		Set<String> mobileSet = new HashSet<>();
		mobileSet.add(bookingId);
		Map<String, BookingInfo> details = getBookingDetails(mobileSet);
		if (details.containsKey(bookingId)) {
			return details.get(bookingId);
		}
		return null;
	}

	public Map<String, BookingInfo> getBookingDetails(Set<String> bookingIds) {
		if (ObjectUtils.isEmpty(bookingIds)) {
			return Collections.emptyMap();
		}
		log.debug("fetchig boookingDetails :{}", bookingIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("booking-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(bookingIds, headers);
		try {
			String url = MessageFormat.format("{0}/booking-service/secure/internal-call/bookings",
					urlConfig.getBaseUrl());
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("response body : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, BookingInfo>>() {
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

	@Deprecated
	// TODO needs to be removed
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
		private Boolean hasUserStartedIt;
		private ChargerInfo chargerInfo;
		private BookingStatus bookingStatus;
		private TimingInfo timingInfo;
		private PriceBreakUp priceBreakup;
		private ElectricityInfo electricityInfo;
		private VehicleInfo vehicleInfo;
		private BookingSource source;
		private BookingViewers viewer;
		private String stoppedBy;
		private String reason;
		private Instant stoppedAt;
		private String appVersion;
		private BookingTypeEnum type;
		private String appSource;
		private String bookedBy;
		private String groupId;
		private String cityId;
		private Instant bookedAt;
		private Long ocppTransactionId;
		private Long userBookingCount;
		private Double predictionVehicleWillGetCharged;
		private Instant updatedAt;
		private Instant createdAt;
		private String cancelledBy;
		private String createdBy;
		private Instant cancelledAt;
		private Boolean isCorrected;

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
				return Instant.now();
			}
		}

		private boolean isBookingCompleteOrCancelled() {
			return bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.CANCELLED);
		}
	}

	public enum BookingTypeEnum {
		INSTANT, SCHEDULED
	}

	@Data
	public static class VehicleInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1234326136840200L;
		private VehicleType type;
		private String registrationNumber;
		private VehicleSOCInfo socInfo = new VehicleSOCInfo();

		@Data
		public static class VehicleSOCInfo implements Serializable {
			private static final long serialVersionUID = 5399195423093175932L;
			private List<VehicleSOC> socTimeInfo;
			private VehicleSOC startSOC;
			private VehicleSOC endSOC;
		}

		@Data
		public static class VehicleSOC implements Serializable {
			private static final long serialVersionUID = -12876526136840200L;
			private int soc;
			private Instant time;
		}
	}

	@Data
	public static class ChargerInfo {
		private String chargerId;
		private String chargerName;
		private String qrCodeValue;
		private SocketDTO socket;
		private Long pricePerUnitAtBookingTime;
		private Long electricityRateAtBookingTime;
		private AddressDTO address;
		private HardwareInfo hardwareInfo;
		private ChargerUsageType usageType;
		private String groupId;
		private DealConfigurationDTO dealConfiguration;
		private Range<Integer> openCloseTimeInSeconds;
		private String locationId;
		private ChargingType chargingType;
		private Boolean sendNotificationToPremiseOwner;
	}

	@Data
	public static class HardwareInfo {
		private String hardwareId;
		private ChargerType chargerType;
		private String imeiNumber;
		private List<HardwareSocket> sockets;
	}

	@Data
	public static class BookingViewers {
		private String primaryChargePointOperatorId;
		private String secondaryChargePointOperatorId;
	}

	@Data
	public static class PriceBreakUp {
		private Long total;
		private Long subTotal;
		private Long gst;
		private Long serviceCharge;
		private Long gstOnServiceCharge;
		private Long parkingFees;
		private Long discount;
		private RefundInfo refundInfo;
	}

	@Data
	public static class ElectricityInfo {
		private long startMeterReading;
		private long endMeterReading;
		private long electricityConsumed;

	}

	@Data
	public static class RefundInfo {
		private Long total;
		private Long subTotal;
		private Long gst;
		private Long serviceCharge;
		private Long gstOnServiceCharge;
		private Long parkingFees;
	}

	@Getter
	@AllArgsConstructor
	public enum BookingStatus {
		IN_PROGRESS, READY_TO_START, COMPLETED, CANCELLED, PAUSED, UPCOMING, INITIATED, PAYMENT_IN_PROGRESS;
	}

	public enum BookingSourceType {
		RFID_TAP, MOBILE_APP, CMS_APP, CZO_APP, HARDWARE_BOOT_UP, WHATSAPP, THIRD_PARTY_APP
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "bookingSourceType", visible = true)
	@JsonSubTypes({ @Type(value = CMSAppBookingSource.class, name = "CMS_APP"),
			@Type(value = MobileAppBookingSource.class, name = "MOBILE_APP"),
			@Type(value = RFIDTapBookingSource.class, name = "RFID_TAP"),
			@Type(value = CZOAppBookingSource.class, name = "CZO_APP"),
			@Type(value = HardwareBootUpBookingSource.class, name = "HARDWARE_BOOT_UP"),
			@Type(value = WhatsappBookingSource.class, name = "WHATSAPP"), })
	public static interface BookingSource {

		BookingSourceType getBookingSourceType();
	}

	@Data
	public static class CMSAppBookingSource implements BookingSource {
		private String cmsUserId;
		private String chargePointOperatorId;
		private BookingSourceType bookingSourceType = BookingSourceType.CMS_APP;
	}

	@Data
	public static class MobileAppBookingSource implements BookingSource {
		private String whiteLabelApp;
		private String appVersion;
		private Rating rating;
		private BookingSourceType bookingSourceType = BookingSourceType.MOBILE_APP;

		@Data
		public static class Rating {
			private Integer booking;
			private Integer charger;
		}
	}

	@Data
	public static class RFIDTapBookingSource implements BookingSource {
		private String rfidCardCode;
		private BookingSourceType bookingSourceType = BookingSourceType.RFID_TAP;
	}

	@Data
	public static class CZOAppBookingSource implements BookingSource {
		private String czoUserId;
		private BookingSourceType bookingSourceType = BookingSourceType.CZO_APP;
	}

	@Data
	public static class HardwareBootUpBookingSource implements BookingSource {
		private String customRFIDCardCode;
		private List<Long> transactionId;
		private BookingSourceType bookingSourceType = BookingSourceType.HARDWARE_BOOT_UP;
	}

	@Data
	public static class WhatsappBookingSource implements BookingSource {
		private BookingSourceType bookingSourceType = BookingSourceType.WHATSAPP;
	}

	@Data
	public static class TimingInfo {
		private Range<Instant> bookedDuration;
		private Range<Instant> actualDuration;
	}

	public static enum ChargingType {
		SLOW, FAST
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
		rabbitMqTemplate.convertAndSend(rabbitQueConfiguration.getStartBookingQueueV3(), request);
	}

	public void stopBookingAsync(IStopBookingRequest request) {
		log.info("stop booking request:{}", request);
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "IInvalid request");
		}
		rabbitMqTemplate.convertAndSend(rabbitQueConfiguration.getStopBookingQueueV3(), request);
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
