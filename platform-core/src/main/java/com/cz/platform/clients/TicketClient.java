package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.cz.platform.security.SecurityConfigProps;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketClient {

	@Autowired
	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	private RestTemplate template;
	private SecurityConfigProps securityProps;
	private UrlConfig urlConfig;
	private ObjectMapper mapper;

	public Boolean hasAnomalyTicket(String bookingId) {
		if (ObjectUtils.isEmpty(bookingId)) {
			return null;
		}
		Map<String, Boolean> details = hasAnomalyTicket(Collections.singleton(bookingId));
		if (details.containsKey(bookingId)) {
			return details.get(bookingId);
		}
		return null;
	}

	public Map<String, Boolean> hasAnomalyTicket(Set<String> bookingIds) {
		log.debug("fetching :{}", bookingIds);
		if (ObjectUtils.isEmpty(bookingIds)) {
			return Collections.emptyMap();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("ticket-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(bookingIds, headers);
		try {
			String url = MessageFormat.format("{0}/ticket-service/secure/internal-call/booking-anomaly-tickets",
					urlConfig.getBaseUrl());
			log.debug("request: {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, Boolean>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Ticket Service api not working");

		}
	}

}
