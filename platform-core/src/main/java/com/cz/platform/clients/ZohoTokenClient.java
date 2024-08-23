package com.cz.platform.clients;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.config.ZohoConfig;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ZohoTokenClient {

	private static String ACCESS_TOKEN = "";

	@Autowired
	private ZohoConfig config;

	@Autowired
	private RestTemplate template;

	public synchronized void refreshToken() throws ApplicationException {
		try {
			log.info("info: {}", config);
			String url = MessageFormat.format("{0}/oauth/v2/token", config.getAuthUrl());
			URIBuilder b = new URIBuilder(url);
			b.addParameter("client_id", config.getClientId());
			b.addParameter("client_secret", config.getClientSecret());
			b.addParameter("redirect_uri", config.getRedirectUri());
			b.addParameter("grant_type", "refresh_token");
			b.addParameter("refresh_token", config.getRefreshToken());
			URI uri = b.build();
			log.info("generating the zoho token {}", config.getAuthUrl());
			ResponseEntity<JsonNode> response = template.postForEntity(uri, null, JsonNode.class);
			log.info("authToken responseBody: {}", response.getBody());
			String accessTokenKey = "access_token";
			JsonNode data = response.getBody();
			if (data.has(accessTokenKey)) {
				String accessToken = data.get(accessTokenKey).asText();
				ACCESS_TOKEN = MessageFormat.format("Zoho-oauthtoken {0}", accessToken);
			} else {
				throw new ValidationException(PlatformExceptionCodes.INVALID_ZOHO_CONFIG);
			}
		} catch (HttpStatusCodeException e) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_ZOHO_CONFIG);
		} catch (URISyntaxException e) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_ZOHO_CONFIG);
		}
	}

	public String getToken() {
		if (ObjectUtils.isEmpty(ACCESS_TOKEN)) {
			refreshToken();
		}
		return ACCESS_TOKEN;
	}

}
