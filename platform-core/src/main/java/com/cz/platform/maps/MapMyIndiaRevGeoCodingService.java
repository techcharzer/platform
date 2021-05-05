package com.cz.platform.maps;

import java.text.MessageFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MapMyIndiaRevGeoCodingService implements RevGeoCodingService {

	private MapMyIndiaConfig config;

	private RestTemplate template;

	private ObjectMapper mapper;

	public RevGeoCodeAddressDTO getAddress(Double lat, Double lon) throws ApplicationException {
		String url = MessageFormat.format(
				"https://apis.mapmyindia.com/advancedmaps/v1/{0}/rev_geocode?lat={1}&lng={2}&region=IND",
				config.getSecretKey(), String.valueOf(lat), String.valueOf(lon));
		try {
			ResponseEntity<JsonNode> response = template.getForEntity(url, JsonNode.class);
			log.info("response : {}", response.getBody());
			JsonNode node = response.getBody();
			if (node.has("results")) {
				JsonNode results = node.get("results");
				if (results.isArray()) {
					for (JsonNode result : (ArrayNode) results) {
						RevGeoCodeAddressDTO address = mapper.convertValue(result, RevGeoCodeAddressDTO.class);
						if (ObjectUtils.isEmpty(address.getCity())) {
							// if city is empty we consider city as district
							String district = result.get("subDistrict").asText();
							address.setCity(district);
						}
						return address;
					}
				}
			}
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(), "No location found");
		} catch (Exception e) {
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(),
					"Unable to fetch the city from coordinates");
		}

	}

}
