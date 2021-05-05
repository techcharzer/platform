package com.cz.platform.maps;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Primary
@Service
@Slf4j
@AllArgsConstructor
public class GoolgeMapsRevGeoCodingService implements RevGeoCodingService {

	private GoogleMapsConfig config;

	private RestTemplate template;

	private ObjectMapper mapper;

	public RevGeoCodeAddressDTO getAddress(Double lat, Double lon) throws ApplicationException {
		String url = MessageFormat.format("https://maps.googleapis.com/maps/api/geocode/json?latlng={0},{1}&key={2}",
				String.valueOf(lat), String.valueOf(lon), config.getSecretKey());
		try {
			ResponseEntity<JsonNode> response = template.getForEntity(url, JsonNode.class);
			log.info("response : {}", response.getBody());
			JsonNode node = response.getBody();
			if (node.has("results")) {
				JsonNode results = node.get("results");
				if (results.isArray()) {
					for (JsonNode result : (ArrayNode) results) {
						GoogleMapsResponse code = mapper.convertValue(result, GoogleMapsResponse.class);
						RevGeoCodeAddressDTO geoCodeAddress = new RevGeoCodeAddressDTO();
						geoCodeAddress.setFormatted_address(code.getFormatted_address());
						for (AddressComponents components : code.getAddress_components()) {
							if (components.getTypes().contains("administrative_area_level_2")) {
								geoCodeAddress.setCity(components.getLong_name());
							} else if (components.getTypes().contains("administrative_area_level_1")) {
								geoCodeAddress.setState(components.getLong_name());
							} else if (components.getTypes().contains("postal_code")) {
								geoCodeAddress.setPincode(components.getLong_name());
							} else if (components.getTypes().contains("country")) {
								geoCodeAddress.setArea(components.getLong_name());
							}
						}
						return geoCodeAddress;
					}
				}
			}
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(), "No location found");
		} catch (Exception e) {
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(),
					"Unable to fetch the city from coordinates");
		}

	}

	@Data
	private static class GoogleMapsResponse {
		private List<AddressComponents> address_components;
		private String formatted_address;
	}

	@Data
	private static class AddressComponents {
		private String long_name;
		private String short_name;
		private HashSet<String> types = new HashSet<>();
	}

}
