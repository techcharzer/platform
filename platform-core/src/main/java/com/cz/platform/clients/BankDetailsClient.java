package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@RefreshScope
public class BankDetailsClient {

	private RestTemplate template;
	private UrlConfig urlConfig;
	private SecurityConfigProps securityProps;
	private ObjectMapper objectMapper;

	public BankDetailsDTO getBankDetailsByUserId(String userId, String chargePointOperatorId) {
		Set<String> userIds = new HashSet<>();
		userIds.add(userId);
		Map<String, BankDetailsDTO> bankDetails = getBankDetailsByUserId(userIds, chargePointOperatorId);
		return bankDetails.get(userId);
	}

	public Map<String, BankDetailsDTO> getBankDetailsByUserId(Set<String> userIds, String chargePointOperatorId) {
		if (ObjectUtils.isEmpty(userIds)) {
			return Collections.emptyMap();
		}
		log.debug("fetchig userId :{}", userIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		GetBankDetailsByUserIdRequest request = new GetBankDetailsByUserIdRequest();
		request.setChargePointOperatorId(chargePointOperatorId);
		request.setUserIds(userIds);
		HttpEntity<GetBankDetailsByUserIdRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/bank-details/user",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return objectMapper.convertValue(response.getBody(), new TypeReference<Map<String, BankDetailsDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"bank details api not working");
		}
	}

	public Map<String, BankDetailsDTO> getBankDetailsById(Set<String> bankDetailsIds) {
		if (ObjectUtils.isEmpty(bankDetailsIds)) {
			return Collections.emptyMap();
		}
		log.debug("fetchig userId :{}", bankDetailsIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		GetBankDetailsByBankIdRequest request = new GetBankDetailsByBankIdRequest();
		request.setBankDetailsIds(bankDetailsIds);
		HttpEntity<GetBankDetailsByBankIdRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/bank-details",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return objectMapper.convertValue(response.getBody(), new TypeReference<Map<String, BankDetailsDTO>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"bank details api not working");
		}
	}

	public BankDetailsDTO getBankDetailsById(String bankDetailsId) {
		Set<String> userIds = new HashSet<>();
		userIds.add(bankDetailsId);
		Map<String, BankDetailsDTO> bankDetails = getBankDetailsById(userIds);
		return bankDetails.get(bankDetailsId);
	}

	@Data
	public static class GetBankDetailsByUserIdRequest {
		private Set<String> userIds;
		private String chargePointOperatorId;
	}

	@Data
	public static class GetBankDetailsByBankIdRequest {
		private Set<String> bankDetailsIds;
	}

	@Data
	public static class BankDetailsDTO {
		private String id;
		private String accountHolderName;
		private String accountNumber;
		private String bankName;
		private String razorpayAccountId;
		private String ifscCode;

		public String getIfscCode() {
			return StringUtils.upperCase(ifscCode);
		}

		public void setIfscCode(String ifscCode) {
			this.ifscCode = ifscCode;
		}

	}

}
