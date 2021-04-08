package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CityClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	public CityDTO getCityById(Long cityId) throws ApplicationException {
		if (ObjectUtils.isEmpty(cityId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid cityId");
		}
		log.debug("fetchig cityId :{}", cityId);
		try {
			String url = MessageFormat.format("{0}/config/city/{1}", urlConfig.getBaseUrl(), String.valueOf(cityId));
			ResponseEntity<CityDTO> response = template.getForEntity(url, CityDTO.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
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
}
