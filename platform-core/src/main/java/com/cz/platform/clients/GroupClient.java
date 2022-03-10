package com.cz.platform.clients;

import java.text.MessageFormat;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.GroupDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GroupClient {

	private RestTemplate template;

	private SecurityConfigProps securityProps;
	private PlatformCommonService platformCommonService;
	private UrlConfig urlConfig;

	public GroupDTO getGroupById(String groupId) {
		log.debug("fetching: {}", groupId);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/group/{1}", urlConfig.getBaseUrl(), groupId);
			log.debug("request : {} body and headers {}", url, entity);
			ResponseEntity<GroupDTO> response = template.exchange(url, HttpMethod.GET, entity, GroupDTO.class);
			log.debug("response : {}", response.getBody());
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			if (platformCommonService.handle404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"user service not working.");
		}

	}

}