package com.cz.platform.maps;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GoogleMapsApiClient implements RevGeoCodingService {

	private GoogleMapsConfig config;

	private RestTemplate template;

	private ObjectMapper mapper;

	public RevGeoCodeAddressDTO getAddress(Double lat, Double lon) throws ApplicationException {
		String url = MessageFormat.format("https://maps.googleapis.com/maps/api/geocode/json?latlng={0},{1}&key={2}",
				String.valueOf(lat), String.valueOf(lon), config.getSecretKey());
		try {
			ResponseEntity<JsonNode> response = template.getForEntity(url, JsonNode.class);
			JsonNode node = response.getBody();
			if (node.has("error_message")) {
				String errorMessage = node.get("error_message").asText();
				throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(), errorMessage);
			} else if (node.has("results")) {
				log.debug("response : {}", response.getBody());
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
			log.debug("no error message or no result found : {}", response.getBody());
			return null;
		} catch (Exception e) {
			log.error("error occured while fetching the address.", e);
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(),
					"Error occured while fetching the address from coordinates.");
		}
	}

	public DistanceAndDurationDTO getDistance(GeoCoordinatesDTO origin, GeoCoordinatesDTO destination) {
		DistanceDurationRequest request = new DistanceDurationRequest();
		request.setDestination(destination);
		request.setOrigin(origin);
		request.setRequestId("single");
		Map<String, DistanceAndDurationDTO> response = getDistance(Collections.singletonList(request));
		return response.get("single");
	}

	public Map<String, DistanceAndDurationDTO> getDistance(List<DistanceDurationRequest> distanceRequests) {
		log.info("request : {}", distanceRequests);
		Map<String, DistanceAndDurationDTO> map = new HashMap<>();
		String url = createUrl(distanceRequests);
		log.info("url: {}", url);
		try {
			ResponseEntity<JsonNode> response = template.getForEntity(url, JsonNode.class);
			JsonNode node = response.getBody();
			if (node.has("error_message")) {
				String errorMessage = node.get("error_message").asText();
				throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(), errorMessage);
			} else if (node.has("rows")) {
				log.debug("response : {}", response.getBody());
				JsonNode rowsNode = node.get("rows");
				if (rowsNode.isArray()) {
					for (int index = 0; index < distanceRequests.size(); ++index) {
						DistanceDurationRequest request = distanceRequests.get(index);
						ArrayNode rows = (ArrayNode) rowsNode;
						JsonNode row = rows.get(index);
						ArrayNode elements = (ArrayNode) row.get("elements");
						JsonNode element = elements.get(index);
						if (element.has("status") && StringUtils.equals(element.get("status").asText(), "OK")) {
							DistanceAndDurationDTO distanceDuration = mapper.convertValue(element,
									DistanceAndDurationDTO.class);
							map.put(request.getRequestId(), distanceDuration);
						}
					}
				}
				return map;
			}
			log.debug("no error message or no result found : {}", response.getBody());
			return null;
		} catch (Exception e) {
			log.error("error occured while fetching the address.", e);
			throw new ApplicationException(PlatformExceptionCodes.SERVICE_NOT_WORKING.getCode(),
					"Error occured while fetching distance from between the coordinates.");
		}

	}

	private String createUrl(List<DistanceDurationRequest> requests) {
		StringBuilder builder = new StringBuilder();
		builder.append("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
		String separator = "";
		for (DistanceDurationRequest request : requests) {
			builder.append(separator).append(request.getOrigin().getLat()).append(',')
					.append(request.getOrigin().getLon());
			separator = "|";
		}
		builder.append("&destinations=");
		separator = "";
		for (DistanceDurationRequest request : requests) {
			builder.append(separator).append(request.getDestination().getLat()).append(',')
					.append(request.getDestination().getLon());
			separator = "|";
		}
		builder.append("&key=").append(config.getSecretKey());
		return builder.toString();
	}

	@Data
	public static class DistanceDurationRequest {
		String requestId;
		private GeoCoordinatesDTO origin;
		private GeoCoordinatesDTO destination;
	}

	@Data
	public static class DistanceAndDurationDTO {
		private Distance distance;
		private Duration duration;
	}

	@Data
	public static class Distance {
		@JsonProperty("text")
		private String distanceInText;
		@JsonProperty("value")
		private Long valueInMeters;
	}

	@Data
	public static class Duration {
		@JsonProperty("text")
		private String timeInText;
		@JsonProperty("value")
		private Long valueInMinutes;
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

	@Override
	public RevGeoCodeAddressDTO getAddressIgnoreError(Double lat, Double lon) {
		try {
			return getAddress(lat, lon);
		} catch (Exception e) {
			log.error("error occured while fetching the address", e);
			return null;
		}
	}

}
