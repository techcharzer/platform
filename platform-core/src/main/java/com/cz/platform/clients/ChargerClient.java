package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.ChargerDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChargerClient {

	@Autowired
	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	private RestTemplate template;

	@Autowired
	private UrlConfig urlConfig;

	@Autowired
	private SecurityConfigProps securityProps;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PlatformCommonService platformCommonService;

	public ChargerDTO getChargerById(String chargerId) {
		log.debug("fetchig :{}", chargerId);
		if (ObjectUtils.isEmpty(chargerId)) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("charger-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/charger-service/secure/internal-call/v2/charger/{1}",
					urlConfig.getBaseUrl(), chargerId);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<ChargerDTO> response = template.exchange(url, HttpMethod.GET, entity, ChargerDTO.class);
			log.debug("response : {}", response);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (platformCommonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Charger api not working");
		}
	}

	public ChargerDTO getChargerByHardwareId(String hardwareId) {
		log.debug("fetchig charger via hardwareId :{}", hardwareId);
		if (ObjectUtils.isEmpty(hardwareId)) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("charger-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/charger-service/secure/internal-call/v2/charger/hardwareId/{1}",
					urlConfig.getBaseUrl(), hardwareId);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<ChargerDTO> response = template.exchange(url, HttpMethod.GET, entity, ChargerDTO.class);
			log.debug("response : {}", response);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (platformCommonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Charger api not working");
		}
	}

	public Map<String, ChargerDTO> getChargerById(Set<String> chargerIds) {
		if (ObjectUtils.isEmpty(chargerIds)) {
			return Collections.emptyMap();
		}
		MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
		for (String chargerId : chargerIds) {
			filters.add("id", chargerId);
		}
		List<ChargerDTO> page = getChargerByFilter(filters, PageRequest.of(0, chargerIds.size()));
		Map<String, ChargerDTO> map = new HashMap<>();
		for (ChargerDTO obj : page) {
			map.put(obj.getChargerId(), obj);
		}
		return map;
	}

	public List<ChargerDTO> getChargerByFilter(MultiValueMap<String, String> queryParams, Pageable page) {
		if (ObjectUtils.isEmpty(queryParams)) {
			return null;
		}
		log.debug("fetchig :{}", queryParams);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("charger-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/charger-service/secure/internal-call/v2/charger",
					urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			queryParams.add("page", String.valueOf(page.getPageNumber()));
			queryParams.add("size", String.valueOf(page.getPageSize()));
			builder.queryParams(queryParams);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity,
					JsonNode.class);
			return mapper.convertValue(response.getBody().get("content"), new TypeReference<List<ChargerDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Charger api not working");
		}
	}

	public ChargerDTO[] getZoneChargers(String zoneId) {
		if (ObjectUtils.isEmpty(zoneId)) {
			return new ChargerDTO[0];
		}
		log.debug("fetchig :{}", zoneId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("charger-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/charger-service/secure/internal-call/charger/zone/{1}",
					urlConfig.getBaseUrl(), zoneId);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<ChargerDTO[]> response = template.exchange(url, HttpMethod.GET, entity, ChargerDTO[].class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Charger api not working");
		}
	}

	public ChargerDTO[] getGroupChargers(String groupId) {
		if (ObjectUtils.isEmpty(groupId)) {
			return new ChargerDTO[0];
		}
		log.debug("fetchig :{}", groupId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("charger-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/charger-service/secure/internal-call/charger/group/{1}",
					urlConfig.getBaseUrl(), groupId);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<ChargerDTO[]> response = template.exchange(url, HttpMethod.GET, entity, ChargerDTO[].class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Charger api not working");
		}
	}

}
