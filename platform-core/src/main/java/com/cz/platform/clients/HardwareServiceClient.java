package com.cz.platform.clients;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cz.platform.PlatformConstants;
import com.cz.platform.charger.configuration.GlobalChargerHardwareInfo;
import com.cz.platform.config.QueueConfiguration;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.LoggerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class HardwareServiceClient {

	private RestTemplate template;
	private SecurityConfigProps securityProps;
	private UrlConfig urlConfig;
	private ObjectMapper mapper;
	private PlatformCommonService platformCommonService;
	private CustomRabbitMQTemplate rabbitMqTemplate;
	private static final QueueConfiguration EXECUTE_COMMAND_QUEUE_CONFIG = new QueueConfiguration();
	private static final QueueConfiguration UPDATE_HARDWARE_TRACKING = new QueueConfiguration();

	@PostConstruct
	private void fill() {
		EXECUTE_COMMAND_QUEUE_CONFIG.setQueueName("execute_command");
		EXECUTE_COMMAND_QUEUE_CONFIG.setExchangeName("hardware_service");
		EXECUTE_COMMAND_QUEUE_CONFIG.setRoutingKey("execute_command");

		UPDATE_HARDWARE_TRACKING.setQueueName("update_hardware_tracking");
		UPDATE_HARDWARE_TRACKING.setExchangeName("update_hardware_tracking");
		UPDATE_HARDWARE_TRACKING.setRoutingKey("update_hardware_tracking");
	}

	public HardwareStatusDTO getChargerOnline(String hardwareId) {
		Set<String> hardwareIdSet = new HashSet<>();
		hardwareIdSet.add(hardwareId);
		Map<String, HardwareStatusDTO> map = getChargerOnline(hardwareIdSet);
		return map.get(hardwareId);
	}

	public Map<String, HardwareStatusDTO> getChargerOnline(Set<String> hardwareIds) {
		if (ObjectUtils.isEmpty(hardwareIds)) {
			return new HashMap<>();
		}
		log.debug("fetchig hardware status :{}", hardwareIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(hardwareIds, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/internal-call/hardware/status/v2",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, HardwareStatusDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"ccu api not working");
		}
	}

	public GlobalChargerHardwareInfo getHardwareInfo(String hardwareId) {
		if (ObjectUtils.isEmpty(hardwareId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid hardwareId");
		}
		log.debug("fetchig hardwareInfo :{}", hardwareId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/internal-call/hardware/{1}", urlConfig.getBaseUrl(),
					hardwareId);
			log.debug("request for fetchig details : {} body and headers {}", url, entity);
			ResponseEntity<GlobalChargerHardwareInfo> response = template.exchange(url, HttpMethod.GET, entity,
					GlobalChargerHardwareInfo.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (platformCommonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"ccu api not working");
		}
	}

	public MeterValue getMeterValues(String hardwareId, String socketId) {
		List<MeterValueRequest> request = new ArrayList<>();
		request.add(new MeterValueRequest(hardwareId, socketId));
		MultipleMeterValue response = getMeterValues(request);
		return response.get(hardwareId, socketId);
	}

	@Data
	@AllArgsConstructor
	public static class MeterValueRequest {
		private String hardwareId;
		private String socketId;
	}

	public MultipleMeterValue getMeterValues(List<MeterValueRequest> hardwareIds) {
		if (ObjectUtils.isEmpty(hardwareIds)) {
			return new MultipleMeterValue(null);
		}
		log.debug("fetchig meter values :{}", hardwareIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<List<MeterValueRequest>> entity = new HttpEntity<>(hardwareIds, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/internal-call/hardware/meter-value",
					urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			log.debug("request for fetchig details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity,
					JsonNode.class);
			Map<String, MeterValue> meterValues = mapper.convertValue(response.getBody(),
					new TypeReference<Map<String, MeterValue>>() {
					});
			return new MultipleMeterValue(meterValues);
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"ccu api not working");
		}
	}

	public void startCharging(StartChargingDTO startCommand) {
		log.debug("start charging :{}, socketId: {}", startCommand);
		HardwareStatusDTO online = getChargerOnline(startCommand.getHardwareId());
		if (!ObjectUtils.isEmpty(online) && BooleanUtils.isTrue(online.getIsOnline())) {
			CommandDTO commandDTO = new CommandDTO();
			commandDTO.setCommand(CommandType.START_BOOKING);
			commandDTO.setCommandData(startCommand);
			commandDTO.setHardwareId(startCommand.getHardwareId());
			commandDTO.setSocketId(startCommand.getSocketId());
			commandDTO.setUserId(startCommand.getUserId());
			executeCommand(commandDTO);
		} else {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Charger is offline. Please restart, wait and retry start charging.");
		}
	}

	private void executeCommand(CommandDTO command) {
		String key = MessageFormat.format("HARDWARE_EXECUTE_COMMAND_WAIT_{0}_{1}", command.getHardwareId(),
				command.getSocketId());
		// addded wait timme of 10 seconds for each hardware and socket combination to
		// prevent confusion to the charger. TECH-T1172
		platformCommonService.takeLock(key, 10,
				"Your old request is being processed please wait for 10 seconds and try again.", LoggerType.DO_NOT_LOG);
		rabbitMqTemplate.convertAndSend(EXECUTE_COMMAND_QUEUE_CONFIG, command);
	}

	public void stopCharging(StopChargingDTO stopCharging) {
		log.debug("stop charging :{}, socketId: {}", stopCharging);
		CommandDTO commandDTO = new CommandDTO();
		commandDTO.setCommand(CommandType.STOP);
		commandDTO.setCommandData(stopCharging);
		commandDTO.setHardwareId(stopCharging.getHardwareId());
		commandDTO.setSocketId(stopCharging.getSocketId());
		commandDTO.setUserId(stopCharging.getUserId());
		executeCommand(commandDTO);
	}

	@Data
	private static class CommandDTO {
		private String hardwareId;
		private String socketId;
		private String userId;
		private CommandType command;
		@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "command")
		@JsonSubTypes({ @Type(value = StartChargingDTO.class, name = "START_BOOKING"),
				@Type(value = StopChargingDTO.class, name = "STOP"),
				@Type(value = StopChargingDTO.class, name = "PAUSE_BOOKING") })
		private CommandData commandData;
	}

	private enum CommandType {
		STOP, START_BOOKING, PAUSE_BOOKING
	}

	@Data
	public static class StopChargingDTO implements CommandData {
		private String hardwareId;
		private String socketId;
		private String userId;
		private String reasonForStopping;
	}

	@Data
	public static class StartChargingDTO implements CommandData {
		private String hardwareId;
		private String socketId;
		private String userId;
		private Instant startTime;
		private Instant endTime;
		private String bookingId;
		private Double maxConsumption;
	}

	public static interface CommandData {

	}

	public void updateHardwareOwnerShip(String hardwareId, String userId, Instant time,
			HardwareTrackingInfoType infoType, HardwareTrackingInfoTypeData infoData) {
		UpdateHardwareTracking ownership = new UpdateHardwareTracking();
		ownership.setInfoType(infoType);
		ownership.setUserId(userId);
		ownership.setCreatedAt(time);
		ownership.setHardwareId(hardwareId);
		ownership.setInfoData(infoData);
		updateHardwareOwnerShip(ownership);
	}

	public void updateHardwareOwnerShip(UpdateHardwareTracking ownership) {
		log.info("request: {}", ownership);
		validateRequest(ownership);
		rabbitMqTemplate.convertAndSend(UPDATE_HARDWARE_TRACKING, ownership);
	}

	private void validateRequest(UpdateHardwareTracking ownership) {
		if (ObjectUtils.isEmpty(ownership)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid ownership");
		}
		if (ObjectUtils.isEmpty(ownership.getHardwareId())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid hardwareId");
		}
		if (ObjectUtils.isEmpty(ownership.getInfoType())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid infoType");
		}
		if (ObjectUtils.isEmpty(ownership.getInfoData())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid infoData");
		}
	}

	@Data
	public static class UpdateHardwareTracking {
		private HardwareTrackingInfoType infoType;
		private String hardwareId;
		@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "infoType")
		@JsonSubTypes({ @Type(value = WarehouseHardwareInventoryTypeData.class, name = "WAREHOUSE"),
				@Type(value = TechnicianMobileNumberHardwareInventoryTypeData.class, name = "TECHNICIAN"),
				@Type(value = DeliveryHardwareInventoryTypeData.class, name = "DELIVERY"),
				@Type(value = InstalledAtInventoryTypeData.class, name = "INSTALLED_AT"),
				@Type(value = VendorInventoryTypeData.class, name = "VENDOR"),
				@Type(value = HostLocationInventoryTypeData.class, name = "HOST_LOCATION") })
		private HardwareTrackingInfoTypeData infoData;
		private Instant createdAt;
		private String userId;
	}

	public enum HardwareTrackingInfoType {
		WAREHOUSE, TECHNICIAN, DELIVERY, INSTALLED_AT, VENDOR, HOST_LOCATION
	}

	public static interface HardwareTrackingInfoTypeData {

	}

	@Data
	public static class WarehouseHardwareInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String warehouseId;
	}

	@Data
	public static class TechnicianHardwareInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String technicianId;
	}

	@Data
	public static class DeliveryHardwareInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String deliveryTicketId;
	}

	@Data
	public static class InstalledAtInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String installedAtId;
	}

	@Data
	public static class VendorInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String vendorId;
	}

	@Data
	public static class HostLocationInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String deliveryTicketId;
	}

	@Data
	public static class TechnicianMobileNumberHardwareInventoryTypeData implements HardwareTrackingInfoTypeData {
		private String mobileNumber;
	}

}
