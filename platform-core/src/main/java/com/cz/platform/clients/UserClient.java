package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

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
import com.cz.platform.dto.ProtectedChargerNetworkGlobalDTO;
import com.cz.platform.dto.UserDetails;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private SecurityConfigProps securityProps;

	private ObjectMapper mapper;

	public UserDetails getUserById(String userId) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid userId");
		}
		log.debug("fetchig userId :{}", userId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/user/{1}", urlConfig.getBaseUrl(), userId);
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<UserDetails> response = template.exchange(url, HttpMethod.GET, entity, UserDetails.class);

			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	public List<ProtectedChargerNetworkGlobalDTO> getUserProtectedNetwork(String userId) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid userId");
		}
		log.debug("fetchig userId :{}", userId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/protected-charger-networks/{1}",
					urlConfig.getBaseUrl(), userId);
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<ProtectedChargerNetworkGlobalDTO[]> response = template.exchange(url, HttpMethod.GET, entity,
					ProtectedChargerNetworkGlobalDTO[].class);
			List<ProtectedChargerNetworkGlobalDTO> list = Arrays.asList(response.getBody());
			log.info("api response : {}", list);
			return list;
		} catch (HttpStatusCodeException exeption) {
			if (handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	public String getAdminProtectedChargerNetworkMapping(String userId) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid userId");
		}
		log.debug("fetchig userId :{}", userId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/admin/protected-charger-network-mapping/{1}",
					urlConfig.getBaseUrl(), userId);
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("api response : {}", response.getBody());
			JsonNode data = response.getBody();
			if (data.has("networkId")) {
				return data.get("networkId").asText();
			}
			return null;
		} catch (HttpStatusCodeException exception) {
			if (handle404Error(exception.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exception.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	private boolean handle404Error(String errorResponse) {
		JsonNode node = null;
		try {
			node = mapper.readTree(errorResponse);
		} catch (JsonProcessingException e) {
			return false;
		}
		if (node != null && node.has("code") && node.get("code").asText().equals(PlatformConstants.CODE_404)) {
			return true;
		}
		return false;
	}
}
