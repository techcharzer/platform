package com.cz.platform.telephone.calls;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CallerDeskOutboundCallService implements OutboundCallService {

	@Autowired
	private CallerDeskConfig config;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	private RestTemplate template;

	@Override
	public void initiateOutboundCall(String agentMobileNumber, String customerNumber) {
		log.info("Agent number: {}, Custommer number: {} ", agentMobileNumber, customerNumber);

		if (ObjectUtils.isEmpty(agentMobileNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid agentMobileNumber");
		}
		if (ObjectUtils.isEmpty(customerNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid customerNumber");
		}
		try {
			MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
			String url = "https://app.callerdesk.io/api/click_to_call_v2";
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			queryParams.add("calling_party_a", agentMobileNumber);
			queryParams.add("calling_party_b", customerNumber);
			queryParams.add("deskphone", getDeskPhone());
			queryParams.add("authcode", config.getAuthkey());
			queryParams.add("call_from_did", "1");
			builder.queryParams(queryParams);
			log.debug("request: {}", builder.toUriString());
			ResponseEntity<String> response = template.exchange(builder.toUriString(), HttpMethod.GET, null,
					String.class);
			log.info("response from callerDesk: {}", response);
			JsonNode node = mapper.readTree(response.getBody());
			if (node.has("type") && StringUtils.equalsAnyIgnoreCase(node.get("type").asText(), "error")) {
				String message = MessageFormat.format("Outbound call failed due to: {0}", node.get("message").asText());
				throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(), message);
			}
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Callerdesk api not working. Reason: " + exeption.getResponseBodyAsString());
		} catch (JsonProcessingException e) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Callerdesk api response unable to parse.");
		}
	}

	public String getDeskPhone() {
		if (ObjectUtils.isEmpty(config.getDeskPhoneNumbers())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid deskPhoneNumbers configuration.");
		}
		RAtomicLong atomicLong = redissonClient.getAtomicLong("CALLER_DESK_OUTBOUND_CALL");
		int val = (int) atomicLong.addAndGet(1);
		int size = config.getDeskPhoneNumbers().size();
		return config.getDeskPhoneNumbers().get(val % size);
	}

}
