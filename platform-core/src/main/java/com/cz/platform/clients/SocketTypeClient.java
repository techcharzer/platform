package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.dto.GlobalSocketTypeDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SocketTypeClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private ObjectMapper mapper;

	public GlobalSocketTypeDTO getSocketBySocketType(String code) throws ApplicationException {
		if (ObjectUtils.isEmpty(code)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid code");
		}
		log.debug("fetchig socketType :{}", code);
		try {
			String url = MessageFormat.format("{0}/config/socket-type/{1}", urlConfig.getBaseUrl(),
					String.valueOf(code));
			ResponseEntity<GlobalSocketTypeDTO> response = template.getForEntity(url, GlobalSocketTypeDTO.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (isSocketTypeNotFoundError(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"socket type Api not working");
		}
	}

	public List<GlobalSocketTypeDTO> getSocketTypes() throws ApplicationException {
		try {
			log.debug("fetching the socket types.");
			String url = MessageFormat.format("{0}/config/socket-type", urlConfig.getBaseUrl());
			ResponseEntity<GlobalSocketTypeDTO[]> response = template.getForEntity(url, GlobalSocketTypeDTO[].class);
			return Arrays.asList(response.getBody());
		} catch (HttpStatusCodeException exeption) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"socket type Api not working");
		}
	}

	private boolean isSocketTypeNotFoundError(String errorResponse) {
		JsonNode node = null;
		try {
			node = mapper.readTree(errorResponse);
		} catch (JsonProcessingException e) {
			return false;
		}
		if (node != null && node.has("code") && node.get("code").asText().equals("CS_1006")) {
			return true;
		}
		return false;
	}
}
