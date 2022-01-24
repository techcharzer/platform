package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.CommonUtility;
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

}
