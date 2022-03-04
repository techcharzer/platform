package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ChargerClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private SecurityConfigProps securityProps;

	private ObjectMapper mapper;

	public ChargerDTO getChargerById(String userIds) {
		MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
		filters.add("id", userIds);
		List<ChargerDTO> page = getChargerByFilter(filters, PageRequest.of(0, 1));
		if (ObjectUtils.isEmpty(page)) {
			return null;
		}
		return page.get(0);
	}

	public Map<String, ChargerDTO> getChargerById(Set<String> userIds) {
		MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
		for (String mobileNumber : userIds) {
			filters.add("id", mobileNumber);
		}
		List<ChargerDTO> page = getChargerByFilter(filters, PageRequest.of(0, userIds.size()));
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
		log.debug("fetchig chargerId :{}", queryParams);
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

			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
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

}
