package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.clients.UrlConfig;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WalletClient {

	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	@Autowired
	private RestTemplate template;

	@Autowired
	private SecurityConfigProps securityProps;

	@Autowired
	private UrlConfig urlConfig;

	@Autowired
	private ObjectMapper mapper;

	public WalletDTO getUserWalletDTO(String userId, String chargePointOperatorId) {
		if (ObjectUtils.isEmpty(userId)) {
			return null;
		}
		Set<String> userIdsSet = new HashSet<>();
		userIdsSet.add(userId);
		Map<String, WalletDTO> details = getUserWalletDTO(userIdsSet, chargePointOperatorId);
		if (details.containsKey(userId)) {
			return details.get(userId);
		}
		return null;
	}

	public Map<String, WalletDTO> getUserWalletDTO(Set<String> userId, String chargePointOperatorId) {
		log.debug("fetchig wallet :{}", userId);
		if (ObjectUtils.isEmpty(userId)) {
			return Collections.emptyMap();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("pw-service"));
		MultipleUserWalletRequest requestData = new MultipleUserWalletRequest();
		requestData.setUserIds(userId);
		requestData.setChargePointOperatorId(chargePointOperatorId);
		HttpEntity<MultipleUserWalletRequest> entity = new HttpEntity<>(requestData, headers);
		try {
			String url = MessageFormat.format("{0}/pw-service/secure/internal-call/user/wallet",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("api response : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, WalletDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to fetch the wallet info.");
		}
	}

	public Map<String, WalletDTO> getUserWalletDTO(Set<String> walletId) {
		log.debug("fetchig wallet :{}", walletId);
		if (ObjectUtils.isEmpty(walletId)) {
			return Collections.emptyMap();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("pw-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(walletId, headers);
		try {
			String url = MessageFormat.format("{0}/pw-service/secure/internal-call/wallet", urlConfig.getBaseUrl());
			log.debug("request for fetchig : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			log.info("api response : {}", response.getBody());
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, WalletDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to fetch the wallet info.");
		}
	}

	@Data
	private static class MultipleUserWalletRequest {
		private Set<String> userIds;
		private String chargePointOperatorId;
	}

	@Data
	private static class WalletRequest {
		private String userId;
		private Long credits;
		private String message;
		private String createdBy;
		private String chargePointOperatorId;
	}

	public WalletDTO createWallet() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("pw-service"));
		HttpEntity<WalletRequest> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/pw-service/secure/internal-call/wallet", urlConfig.getBaseUrl());
			log.debug("request for creating wallet : {} body and headers {}", url, entity);
			ResponseEntity<WalletDTO> response = template.exchange(url, HttpMethod.POST, entity, WalletDTO.class);
			log.info("api response : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Unable to create the wallet.");
		}
	}

	@Data
	public static class WalletDTO {
		private String walletId;
		private Long credits;
	}

}
