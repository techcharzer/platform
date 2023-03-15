package com.cz.platform.clients;

import java.io.Serializable;
import java.text.MessageFormat;

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
import com.cz.platform.dto.AddressDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ChargePointOperatorClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private SecurityConfigProps securityProps;

	private ObjectMapper mapper;

	private PlatformCommonService commonService;

	public ChargePointOperatorDTO getChargePointOperator(String chargePointOperatorId) {
		if (ObjectUtils.isEmpty(chargePointOperatorId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid chargePointOperatorId");
		}
		log.debug("fetching: {}", chargePointOperatorId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/chargePointOperator/{1}",
					urlConfig.getBaseUrl(), chargePointOperatorId);
			log.debug("request: {}, headers {}", url, entity);
			ResponseEntity<ChargePointOperatorDTO> response = template.exchange(url, HttpMethod.GET, entity,
					ChargePointOperatorDTO.class);
			log.info("api response : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exception) {
			if (commonService.handle404Error(exception.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exception.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"charge point api not working");
		}
	}

	public boolean isValidChargePointOperator(String chargePointOperator) {
		return !ObjectUtils.isEmpty(getChargePointOperator(chargePointOperator));
	}

	public void validateChargePointOperator(String chargePointOperator) {
		if (ObjectUtils.isEmpty(getChargePointOperator(chargePointOperator))) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid chargePointOperatorId");
		}
	}

	@Data
	public static class ChargePointOperatorDTO implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6106129986682393403L;
		private String id;
		private String companyName;
		private String website;
		private String gstNumber;
		private AddressDTO address;
	}

}
