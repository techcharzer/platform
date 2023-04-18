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
import com.cz.platform.enums.ActiveInactiveStatus;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WhiteLabelAppClient {

	private RestTemplate template;

	private UrlConfig urlConfig;

	private SecurityConfigProps securityProps;

	private PlatformCommonService commonService;

	public WhiteLabelApplicationConfigurationDTO getWhiteLabelApplicationConfiguration(String chargePointOperatorId) {
		if (ObjectUtils.isEmpty(chargePointOperatorId)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid chargePointOperatorId");
		}
		log.debug("fetchig :{}", chargePointOperatorId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("config-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/config/secure/internal-call/white-label-application/{1}",
					urlConfig.getBaseUrl(), chargePointOperatorId);
			log.debug("request: {}, headers {}", url, entity);
			ResponseEntity<WhiteLabelApplicationConfigurationDTO> response = template.exchange(url, HttpMethod.GET,
					entity, WhiteLabelApplicationConfigurationDTO.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			if (commonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Config service not working.");
		}
	}

	@Data
	public static class WhiteLabelApplicationConfigurationDTO {
		private String id;
		private String chargePointOperatorId;
		private String appName;
		private String downloadLink;
		private String deepLinkDomainPrefixUrl;
		private AndroidConfiguration androidConfiguration;
		private IosConfiguration iosConfiguration;
		private ReferralConfig referralConfig;
		private int walletInitializationCredits;
		private ActiveInactiveStatus status;

		@Data
		public static class ReferralConfig implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1047464315179245425L;
			public Long referrerCreditUnits;
			public Long refereeCreditUnits;
			public Long firstBookingReferrerCreditUnits;
			public String referralMessageTemplate;
		}

		@Data
		public static class AndroidConfiguration {
			private String packageName;
		}

		@Data
		public static class IosConfiguration {
			private Boolean isIosAppLive = false;
			private String iosAppleStoreId;
			private String iosBundleId;
		}

	}
}