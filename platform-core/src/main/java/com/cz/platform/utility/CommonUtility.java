package com.cz.platform.utility;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.cz.platform.dto.CodeValueDTO;
import com.cz.platform.dto.Range;
import com.cz.platform.enums.ChargerType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommonUtility {

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

	public static List<CodeValueDTO<String, String>> getMonthlyFilterValues(int noOfFilters) {
		List<CodeValueDTO<String, String>> list = new ArrayList<>();
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusMonths(1).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusMonths(i).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
			String value = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			list.add(new CodeValueDTO<String, String>(value, FORMATTER.format(start)));
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
