package com.cz.platform.clients;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@RefreshScope
public class CityClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private SecurityConfigProps securityProps;

	private ObjectMapper mapper;

	public CityDTO getCityById(String cityId) throws ApplicationException {
		if (ObjectUtils.isEmpty(cityId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid cityId");
		}
		log.debug("fetchig cityId :{}", cityId);
		try {
			String url = MessageFormat.format("{0}/config/city/{1}", urlConfig.getBaseUrl(), String.valueOf(cityId));
			ResponseEntity<CityDTO> response = template.getForEntity(url, CityDTO.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (isCityNotFoundError(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"city Api not working");
		}
	}

	public List<CityDTO> getCities() throws ApplicationException {
		try {
			log.debug("fetching the cities.");
			String url = MessageFormat.format("{0}/config/city", urlConfig.getBaseUrl());
			ResponseEntity<CityDTO[]> response = template.getForEntity(url, CityDTO[].class);
			return Arrays.asList(response.getBody());
		} catch (HttpStatusCodeException exeption) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"city Api not working");
		}
	}

	public CityDTO getCityByNameAndState(String cityName, String stateName) throws ApplicationException {
		try {
			log.debug("fetching the city by cityName: {} stateName : {}", cityName, stateName);
			String url = MessageFormat.format("{0}/config/city/{1}/state/{2}", urlConfig.getBaseUrl(), cityName,
					stateName);
			ResponseEntity<CityDTO> response = template.getForEntity(url, CityDTO.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (isCityNotFoundError(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"city Api not working");
		}
	}

	public CityDTO saveCity(CityDTO cityDTO) throws ApplicationException {
		if (ObjectUtils.isEmpty(cityDTO)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid/empty request");
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("config-service"));
		HttpEntity<CityDTO> entity = new HttpEntity<>(cityDTO, headers);
		try {
			log.info("saving the city : {}", cityDTO);
			String url = MessageFormat.format("{0}/config/secure/city", urlConfig.getBaseUrl());
			HttpEntity<CityDTO> response = template.exchange(url, HttpMethod.POST, entity, CityDTO.class);
			log.info("response from the server : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"city Api not working");
		}
	}

	private boolean isCityNotFoundError(String errorResponse) {
		JsonNode node = null;
		try {
			node = mapper.readTree(errorResponse);
		} catch (JsonProcessingException e) {
			return false;
		}
		if (node != null && node.has("code") && node.get("code").asText().equals("CS_1005")) {
			return true;
		}
		return false;
	}

	@Data
	public static class CityDTO implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3105294727064442647L;
		private String cityId;
		private String cityName;
		private String state;
		private Integer sortParam;
		private Boolean isActive;
		private String urlSlug;
		private Integer groupId;
		private String imageUrl;
		private Boolean isPopular;
		private Boolean isOperational;
		private Boolean isListedOnWeb;
	}

}
