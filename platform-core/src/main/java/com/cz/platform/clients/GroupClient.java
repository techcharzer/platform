package com.cz.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.GroupDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.security.SecurityConfigProps;
import com.cz.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@RefreshScope
public class GroupClient {

	private RestTemplate template;
	private ObjectMapper mapper;
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
			if (platformCommonService.is404Error(exeption.getResponseBodyAsString())) {
				return null;
			}
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"user service not working.");
		}

	}

	public Map<String, GroupDTO> getGroupById(Set<String> groupIds) {
		if (ObjectUtils.isEmpty(groupIds)) {
			return Collections.emptyMap();
		}
		MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
		int count = 1;
		for (String groupId : groupIds) {
			if (!ObjectUtils.isEmpty(groupId)) {
				filters.add("groupId", groupId);
				++count;
			}
		}
		if (ObjectUtils.isEmpty(filters)) {
			return Collections.emptyMap();
		}
		Page<GroupDTO> page = getGroupByFilter(filters, PageRequest.of(0, count));
		Map<String, GroupDTO> map = new HashMap<>();
		for (GroupDTO userDetails : page.getContent()) {
			map.put(userDetails.getId(), userDetails);
		}
		return map;
	}

	public Page<GroupDTO> getGroupByFilter(MultiValueMap<String, String> queryParams, Pageable pageRequest) {
		if (ObjectUtils.isEmpty(queryParams)) {
			return null;
		}
		log.debug("fetchig userId :{}", queryParams);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/groups", urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			queryParams.add("page", String.valueOf(pageRequest.getPageNumber()));
			queryParams.add("size", String.valueOf(pageRequest.getPageSize()));
			builder.queryParams(queryParams);

			log.debug("request: {}, headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity,
					JsonNode.class);
			List<GroupDTO> list = mapper.convertValue(response.getBody().get("content"),
					new TypeReference<List<GroupDTO>>() {
					});
			int pageNumber = response.getBody().get("pageable").get("pageNumber").asInt();
			int pageSize = response.getBody().get("pageable").get("pageSize").asInt();
			Pageable page = PageRequest.of(pageNumber, pageSize);
			long totalElements = response.getBody().get("totalElements").asLong();
			return new PageImpl<>(list, page, totalElements);
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

}