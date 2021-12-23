package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

import com.cz.platform.dto.CodeValueDTO;
import com.cz.platform.dto.Range;
import com.cz.platform.enums.ChargerType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommonUtility {

	private static final Map<Integer, String> DAYS_MAPPING = new HashMap<>();
	private static final Map<Integer, String> WEEKS_MAPPING = new HashMap<>();

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
			.withZone(ZoneId.systemDefault());

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
			case CHARGE_MOD_BHARAT_AC:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".imeiNumber")).is(chargerControlId);
				break;
			case OCPP_16_JSON_CHARGER:
				valCriteria = Criteria.where(pathOfConfiguration.concat(".chargerBoxId")).is(chargerControlId);
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
		List<CodeValueDTO<String, String>> list = new ArrayList<>();
		getDayFilterValues(1, list);
		getWeekFilterValues(1, list);
		getMonthlyFilterValues(4, list);
		return list;
	}

	public static void getMonthlyFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusMonths(1).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusMonths(i).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Month - {0}", FORMATTER.format(start));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static void getDayFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusDays(i).atStartOfDay().toInstant(ZoneOffset.UTC);
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
		Instant end = nowDate.plusWeeks(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusWeeks(i).atStartOfDay().toInstant(ZoneOffset.UTC);
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

}
