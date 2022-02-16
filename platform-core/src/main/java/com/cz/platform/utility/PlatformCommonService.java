package com.cz.platform.utility;

import org.springframework.stereotype.Service;

import com.cz.platform.PlatformConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PlatformCommonService {

	private ObjectMapper mapper;

	public boolean handle404Error(String errorResponse) {
		JsonNode node = null;
		try {
			node = mapper.readTree(errorResponse);
		} catch (JsonProcessingException e) {
			return false;
		}
		if (node != null && node.has("code") && node.get("code").asText().equals(PlatformConstants.CODE_404)) {
			return true;
		}
		return false;
	}
}
