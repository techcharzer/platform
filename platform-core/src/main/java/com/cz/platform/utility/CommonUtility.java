package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.dto.AddressDTOV2;
import com.cz.platform.dto.AddressData;
import com.cz.platform.dto.CodeValueDTO;
import com.cz.platform.dto.GeoCoordinatesDTO;
import com.cz.platform.dto.HybridAddressDTO;
import com.cz.platform.dto.Image;
import com.cz.platform.dto.PostalAddress;
import com.cz.platform.dto.Range;
import com.cz.platform.enums.ChargerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommonUtility {

	private static final Map<Integer, String> DAYS_MAPPING = new HashMap<>();
	private static final Map<Integer, String> WEEKS_MAPPING = new HashMap<>();
	private static final ZoneOffset INDIA_ZONE_OFFSET = ZoneOffset.ofHoursMinutes(5, 30);

	static {
		DAYS_MAPPING.put(0, "Today");
		DAYS_MAPPING.put(1, "Yesterday");
		DAYS_MAPPING.put(2, "2 days ago");
		DAYS_MAPPING.put(3, "3 days ago");
		DAYS_MAPPING.put(4, "4 days ago");
		DAYS_MAPPING.put(5, "5 days ago");
		DAYS_MAPPING.put(6, "6 days ago");

		WEEKS_MAPPING.put(0, "This Week");
		WEEKS_MAPPING.put(1, "Last Week");
		WEEKS_MAPPING.put(2, "2 weeks ago");
		WEEKS_MAPPING.put(3, "3 weeks ago");
		WEEKS_MAPPING.put(4, "4 weeks ago");
	}

	private CommonUtility() {
	}

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yy", Locale.UK)
			.withZone(PlatformConstants.CURRENT_ZONE_ID);

	public static String getUrlSlug(String str) {
		return ObjectUtils.isEmpty(str) ? str : replaceSpaceWithHyphen(str.toLowerCase());
	}

	public static String replaceSpaceWithHyphen(String str) {
		return ObjectUtils.isEmpty(str) ? str : str.replace(" ", "-");
	}

	public static Range<Integer> parseRange(String range, String regexSplitter) {
		String arr[] = range.split(regexSplitter);
		if (arr.length != 2) {
			return null;
		}
		return new Range<Integer>(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
	}

	public static Range<Long> parseRangeLong(String range, String regexSplitter) {
		String arr[] = range.split(regexSplitter);
		if (arr.length != 2) {
			return null;
		}
		return new Range<Long>(Long.parseLong(arr[0]), Long.parseLong(arr[1]));
	}

	public static Criteria chargerControlIdCriteria(String chargerTypeKeyPath, String pathOfConfiguration,
			String chargerControlId) {
		Criteria criteria = new Criteria();
		List<Criteria> list = new ArrayList<>();
		for (ChargerType val : ChargerType.values()) {
			Criteria inCriteria = new Criteria();
			Criteria valCriteria = null;
			switch (val) {
			case KIRANA_CHARZER_BLE:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".macAddress")).is(chargerControlId);
				break;
			case KIRANA_CHARZER_GSM:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".deviceId")).is(chargerControlId);
				break;
			case KIRANA_CHARZER_FLEXTRON:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".ccuId")).is(chargerControlId);
				break;
			case KIRANA_CHARZER_FLEXTRON_WIFI:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".ccuId")).is(chargerControlId);
				break;
			case CHARGE_MOD_BHARAT_AC:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".imeiNumber")).is(chargerControlId);
				break;
			case OCPP_16_JSON_CHARGER:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".chargerBoxId")).is(chargerControlId);
				break;
			case EV_POINT_CHARGER:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".deviceId")).is(chargerControlId);
				break;
			case OTHER_NETWORK_CHARGER:
				break;
			default:
				break;
			}
			if (!ObjectUtils.isEmpty(valCriteria)) {
				Criteria chargerType = Criteria.where(chargerTypeKeyPath).is(val.name());
				list.add(inCriteria.andOperator(chargerType, valCriteria));
			}
		}
		Criteria[] array = list.toArray(new Criteria[list.size()]);
		criteria.orOperator(array);
		return criteria;
	}

	public static List<CodeValueDTO<String, String>> getRecentTimeFilterValues() {
		return getRecentTimeFilterValues(3, 3, 4);
	}

	public static List<CodeValueDTO<String, String>> getRecentTimeFilterValues(int day, int week, int month) {
		List<CodeValueDTO<String, String>> list = new ArrayList<>();
		if (!DAYS_MAPPING.containsKey(day)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter day configuration");
		}
		if (!WEEKS_MAPPING.containsKey(week)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter week configuration");
		}
		getDayFilterValues(day, list);
		getWeekFilterValues(week, list);
		getMonthlyFilterValues(month, list);
		return list;
	}

	public static void getMonthlyFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusMonths(1).withDayOfMonth(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusMonths(i).withDayOfMonth(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Month - {0}", FORMATTER.format(start));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static void getDayFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusDays(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusDays(i).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Day - {0}", DAYS_MAPPING.get(i));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static List<CodeValueDTO<String, String>> getWeekFilterValues(int noOfFilters,
			List<CodeValueDTO<String, String>> list) {
		LocalDate nowDate = LocalDate.now();
		nowDate = nowDate.minusDays(nowDate.getDayOfWeek().getValue() - 1);
		Instant end = nowDate.plusWeeks(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusWeeks(i).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Week - {0}", WEEKS_MAPPING.get(i));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
		return list;
	}

	public static int getSize(MultiValueMap<String, String> queryParams) {
		int size = 0;
		if (ObjectUtils.isEmpty(queryParams)) {
			return size;
		}
		for (List<String> list : queryParams.values()) {
			if (!ObjectUtils.isEmpty(list)) {
				size += list.size();
			}
		}
		return size;
	}

	public static String getKey(String first, String second) {
		return MessageFormat.format("{0}###{1}", first, second);
	}

	public static Image getDefaultImage(List<Image> images) {
		if (ObjectUtils.isEmpty(images)) {
			Image image = new Image();
			image.setName("Charzer Image");
			image.setRelativePath("static/default_charger_img.jpg");
			return new Image();
		}
		return images.get(0);
	}

	public static void validateAddress(AddressDTOV2 address) {

		if (ObjectUtils.isEmpty(address)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid address");
		}
		if (ObjectUtils.isEmpty(address.getType())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid address.type");
		}
		if (ObjectUtils.isEmpty(address.getData())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid address.data");
		}
		switch (address.getType()) {
		case GPS_COORDINATES:
			validateGeoCoordinates(address.getData());
			break;
		case POSTAL_ADDRESS:
			validatePostalAddress(address.getData());
			break;
		case HYBRID_ADDRESS:
			validateHybridAddress(address.getData());
			break;

		default:
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid addressType");
		}

	}

	private static void validateHybridAddress(AddressData data) {
		HybridAddressDTO address = (HybridAddressDTO) data;
		validateGeoCoordinates(address.getCoordinates());
		if (ObjectUtils.isEmpty(address.getPostalAddress())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.postalAddress");
		}
		if (ObjectUtils.isEmpty(address.getPinCode())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.pinCode");
		}
		if (ObjectUtils.isEmpty(address.getCityId())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.cityId");
		}
	}

	private static void validatePostalAddress(AddressData data) {
		PostalAddress address = (PostalAddress) data;
		if (ObjectUtils.isEmpty(address.getPinCode())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.pinCode");
		}
		if (ObjectUtils.isEmpty(address.getPostalAddress())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.postalAddress");
		}
	}

	private static void validateGeoCoordinates(AddressData addressData) {
		GeoCoordinatesDTO coordinates = (GeoCoordinatesDTO) addressData;
		if (ObjectUtils.isEmpty(coordinates.getLat())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.lat");
		}
		if (ObjectUtils.isEmpty(coordinates.getLon())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid data.lon");
		}
	}

}
