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
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.CommonUtility;
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
	
	@PostConstruct
	private void fill() {
		EXECUTE_COMMAND_QUEUE_CONFIG.setQueueName("execute_command");
		EXECUTE_COMMAND_QUEUE_CONFIG.setExchangeName("hardware_service");
		EXECUTE_COMMAND_QUEUE_CONFIG.setRoutingKey("execute_command");
	}

	public ChargerOnlineDTO getChargerOnline(String hardwareId) {
		Set<String> hardwareIdSet = new HashSet<>();
		hardwareIdSet.add(hardwareId);
		Map<String, ChargerOnlineDTO> map = getChargerOnline(hardwareIdSet);
		return map.get(hardwareId);
	}

	public Map<String, ChargerOnlineDTO> getChargerOnline(Set<String> hardwareIds) {
		if (ObjectUtils.isEmpty(hardwareIds)) {
			return new HashMap<>();
		}
		log.debug("fetchig userId :{}", hardwareIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/charger/online", urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			for (String hardwareId : hardwareIds) {
				builder.queryParam("id", hardwareId);
			}
			log.debug("request for fetchig details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity,
					JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, ChargerOnlineDTO>>() {
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
		log.debug("fetchig userId :{}", hardwareId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/hardware/{1}", urlConfig.getBaseUrl(), hardwareId);
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

	public HardwareStatusInfo getHardwareCurrentStatusInfo(String hardwareId, String socketId) {
		List<CurrentStatusInfoRequest> request = new ArrayList<>();
		request.add(new CurrentStatusInfoRequest(hardwareId, socketId));
		Map<String, HardwareStatusInfo> response = getHardwareCurrentStatusInfo(request);
		return response.get(CommonUtility.getKey(hardwareId, socketId));
	}

	@Data
	@AllArgsConstructor
	public static class CurrentStatusInfoRequest {
		private String hardwareId;
		private String socketId;
	}

	public Map<String, HardwareStatusInfo> getHardwareCurrentStatusInfo(List<CurrentStatusInfoRequest> hardwareIds) {
		if (ObjectUtils.isEmpty(hardwareIds)) {
			return new HashMap<>();
		}
		log.debug("fetchig userId :{}", hardwareIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ccu-service"));
		HttpEntity<List<CurrentStatusInfoRequest>> entity = new HttpEntity<>(hardwareIds, headers);
		try {
			String url = MessageFormat.format("{0}/ccu/secure/hardware/status", urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			log.debug("request for fetchig details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(builder.toUriString(), HttpMethod.POST, entity,
					JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, HardwareStatusInfo>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"ccu api not working");
		}
	}

	public void startCharging(StartChargingDTO startCommand) {
		log.debug("start charging :{}, socketId: {}", startCommand);
		CommandDTO commandDTO = new CommandDTO();
		commandDTO.setCommand(CommandType.START_BOOKING);
		commandDTO.setCommandData(startCommand);
		commandDTO.setHardwareId(startCommand.getHardwareId());
		commandDTO.setSocketId(startCommand.getSocketId());
		commandDTO.setUserId(startCommand.getUserId());
		executeCommand(commandDTO);
	}

	private void executeCommand(CommandDTO command) {
		rabbitMqTemplate.convertAndSend(EXECUTE_COMMAND_QUEUE_CONFIG, command);
	}

	public void stopCharging(StopChargingDTO stopCharging) {
		log.debug("stop charging :{}, socketId: {}", stopCharging);
		CommandDTO commandDTO = new CommandDTO();
		commandDTO.setCommand(CommandType.STOP);
		commandDTO.setCommandData(stopCharging);
		commandDTO.setHardwareId(stopCharging.getHardwareId());
		commandDTO.setSocketId(stopCharging.getSocketId());
		commandDTO.setUserId(null);
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

}
