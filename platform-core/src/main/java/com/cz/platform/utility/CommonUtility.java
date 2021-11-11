package com.cz.platform.utility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import com.cz.platform.dto.Range;
import com.cz.platform.enums.ChargerType;

public final class CommonUtility {

	private CommonUtility() {
	}

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

	public static Criteria chargerControlIdCriteria(String chargerTypeKey, String keyOfConfiguration,
			String chargerControlId) {
		Criteria criteria = new Criteria();
		List<Criteria> list = new ArrayList<>();
		for (ChargerType val : ChargerType.values()) {
			Criteria inCriteria = new Criteria();
			Criteria valCriteria = null;
			switch (val) {
			case KIRANA_CHARZER_BLE:
				valCriteria = Criteria.where(keyOfConfiguration.concat(".macAddress")).is(chargerControlId);
				break;
			case KIRANA_CHARZER_GSM:
				valCriteria = Criteria.where(keyOfConfiguration.concat(".deviceId")).is(chargerControlId);
				break;
			case KIRANA_CHARZER_FLEXTRON:
				valCriteria = Criteria.where(keyOfConfiguration.concat(".ccuId")).is(chargerControlId);
				break;
			case CHARGE_MOD_BHARAT_AC:
				break;
			case OCPP_16_JSON_CHARGER:
				valCriteria = Criteria.where(keyOfConfiguration.concat(".chargerBoxId")).is(chargerControlId);
				break;
			case OTHER_NETWORK_CHARGER:
				break;
			default:
				break;
			}
			if (!ObjectUtils.isEmpty(valCriteria)) {
				Criteria chargerType = Criteria.where(chargerTypeKey).is(val.name());
				list.add(inCriteria.andOperator(chargerType, valCriteria));
			}
		}
		Criteria[] array = list.toArray(new Criteria[list.size()]);
		criteria.orOperator(array);
		return criteria;
	}

}
