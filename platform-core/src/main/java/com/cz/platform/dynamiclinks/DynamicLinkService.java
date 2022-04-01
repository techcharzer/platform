package com.cz.platform.dynamiclinks;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cz.platform.config.DynamicLinkConfig;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DynamicLinkService {

	private RestTemplate restTemplate;

	private DynamicLinkConfig deeplinkConfig;

	private ObjectMapper mapper;

	public String getDeeplink(Map<String, String> mapOfRequestParamsInDeepLink) throws ApplicationException {
		log.info("deep link parameters : {}", mapOfRequestParamsInDeepLink);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(deeplinkConfig.getDeeplinkBaseUrl());
		for (Entry<String, String> entry : mapOfRequestParamsInDeepLink.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue());
		}

		String deeplinkLongUrl = builder.toUriString();
		DynamicLinkRequest dynamicLinkRequest = new DynamicLinkRequest();
		DeeplinkRequest request = new DeeplinkRequest();
		request.setDomainUriPrefix(deeplinkConfig.getDomainPrefixUrl());
		request.setLink(deeplinkLongUrl);

		AndroidInfo androidInfo = new AndroidInfo();
		androidInfo.setAndroidPackageName(deeplinkConfig.getAndroidPackageName());
		request.setAndroidInfo(androidInfo);

		IosInfo iosInfo = new IosInfo();
		iosInfo.setIosAppStoreId(deeplinkConfig.getIosAppStoreId());
		iosInfo.setIosBundleId(deeplinkConfig.getIosBundleId());
		request.setIosInfo(iosInfo);

		dynamicLinkRequest.setDynamicLinkInfo(request);
		return generateDynamicLink(dynamicLinkRequest);
	}

	private String generateDynamicLink(DynamicLinkRequest request) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<DynamicLinkRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = MessageFormat.format("https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key={0}",
					deeplinkConfig.getSecretKey());
			log.debug("request : {}", entity.toString());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			log.debug("response from firebase : {}", response.getBody());
			JsonNode node = mapper.readTree(response.getBody());
			if (node.has("shortLink")) {
				String deeplink = node.get("shortLink").asText();
				log.info("deeplink created : {}", deeplink);
				return deeplink;
			} else {
				throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
						"No Short link found in response");
			}
		} catch (HttpStatusCodeException e) {
			log.error("response : {}", e.getResponseBodyAsString(), e);
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"Error occured while fetching the short deeplink", e);
		} catch (JsonProcessingException e) {
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"error occured while processing json.", e);
		}
	}

	@Data
	private class DynamicLinkRequest {
		private DeeplinkRequest dynamicLinkInfo;
	}

	@Data
	private class DeeplinkRequest {
		private String domainUriPrefix;
		private String link;
		private AndroidInfo androidInfo;
		private IosInfo iosInfo;
	}

	@Data
	private class AndroidInfo {
		private String androidPackageName;
	}

	@Data
	private class IosInfo {
		private String iosBundleId;
		private String iosAppStoreId;
	}

	public String getDeeplink(String page, String id) throws ApplicationException {
		Map<String, String> map = new HashMap<>();
		map.put("page", page);
		map.put("id", id);
		return getDeeplink(map);
	}

}
