package com.cz.platform.clients;

import java.text.MessageFormat;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZoneClient {

	@Autowired
	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	private RestTemplate template;

	@Autowired
	private UrlConfig urlConfig;

	@Autowired
	private SecurityConfigProps securityProps;

	@Autowired
	private PlatformCommonService platformCommonService;

	public ZoneDTO getZoneById(String zoneId) {
		log.debug("fetchig :{}", zoneId);
		if (ObjectUtils.isEmpty(zoneId)) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("lms-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/lms/secure/internal-call/zone/{1}", urlConfig.getBaseUrl(), zoneId);

			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<ZoneDTO> response = template.exchange(url, HttpMethod.GET, entity, ZoneDTO.class);
			log.debug("response : {}", response);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (platformCommonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Zone api not working");
		}
	}

	@Data
	public static class ZoneDTO {
		private String id;
		private String name;
		private SecurityRiskEnum securityRisk;
		private GeoCoordinatesDTO coordinates;
		private String source;
		private String expectedNoOfEVUsers;
		private String cityId;
		private Integer radius;
		private Boolean isActive;
		private String notes;
		private String createdBy;
		private Instant createdAt;

	}

	public static enum SecurityRiskEnum {
		HIGH_RISK, MODERATE_RISK, SAFE
	}

}
