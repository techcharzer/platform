package com.cz.platform.clients;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.whitelabel.WhiteLabelAppConfig;
import com.cz.platform.whitelabel.WhiteLabelAppTypeEnum;
import com.cz.platform.whitelabel.WhiteLabelConfigurationDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WhiteLabelAppClient {

	private WhiteLabelAppConfig config;

//	private RestTemplate template;
//
//	private UrlConfig urlConfig;
//
//	private SecurityConfigProps securityProps;
//
//	private PlatformCommonService commonService;

	public WhiteLabelConfigurationDTO getWhiteLabelConfig(WhiteLabelAppTypeEnum whiteLabelApp) {
		if (ObjectUtils.isEmpty(whiteLabelApp)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid whiteLabelApp");
		}
		log.debug("fetchig :{}", whiteLabelApp);
		return config.getWhiteLabelConfiguration().get(whiteLabelApp);
//		HttpHeaders headers = new HttpHeaders();
//		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("config-service"));
//		HttpEntity<String> entity = new HttpEntity<>(null, headers);
//		try {
//			String url = MessageFormat.format("{0}/config/secure/white-label-app/{1}", urlConfig.getBaseUrl(),
//					whiteLabelApp);
//			log.debug("request: {}, headers {}", url, entity);
//			ResponseEntity<WhiteLabelConfigurationDTO> response = template.exchange(url, HttpMethod.GET, entity,
//					WhiteLabelConfigurationDTO.class);
//			return response.getBody();
//		} catch (HttpStatusCodeException exeption) {
//			if (commonService.handle404Error(exeption.getResponseBodyAsString())) {
//				return null;
//			}
//			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
//			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
//					"Config service not working.");
//		}
	}

}
