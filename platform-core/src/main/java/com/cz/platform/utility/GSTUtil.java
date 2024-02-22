package com.cz.platform.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

public class GSTUtil {

	public static final Map<String, String> GST_STATE_CODE_MAP = new HashMap<>();

	static {
		fillMap();
	}

	private static void fillMap() {
		GST_STATE_CODE_MAP.put("35", "Andaman and Nicobar Islands");
		GST_STATE_CODE_MAP.put("28", "Andra Pradesh");
		GST_STATE_CODE_MAP.put("37", "Andhra Pradesh (New)");
		GST_STATE_CODE_MAP.put("12", "Arunachal Pradesh");
		GST_STATE_CODE_MAP.put("18", "Assam");
		GST_STATE_CODE_MAP.put("10", "Bihar");
		GST_STATE_CODE_MAP.put("04", "Chandigarh");
		GST_STATE_CODE_MAP.put("22", "Chhattisgarh");
		GST_STATE_CODE_MAP.put("26", "Dadra and Nagar Haveli");
		GST_STATE_CODE_MAP.put("25", "Daman and Diu");
		GST_STATE_CODE_MAP.put("07", "Delhi");
		GST_STATE_CODE_MAP.put("30", "Goa");
		GST_STATE_CODE_MAP.put("24", "Gujarat");
		GST_STATE_CODE_MAP.put("06", "Haryana");
		GST_STATE_CODE_MAP.put("02", "Himachal Pradesh");
		GST_STATE_CODE_MAP.put("01", "Jammu and Kashmir");
		GST_STATE_CODE_MAP.put("20", "Jharkhand");
		GST_STATE_CODE_MAP.put("29", "Karnataka");
		GST_STATE_CODE_MAP.put("32", "Kerala");
		GST_STATE_CODE_MAP.put("38", "Ladhak");
		GST_STATE_CODE_MAP.put("31", "Lakshadweep Island");
		GST_STATE_CODE_MAP.put("23", "Madhya Pradesh");
		GST_STATE_CODE_MAP.put("27", "Maharashtra");
		GST_STATE_CODE_MAP.put("14", "Manipur");
		GST_STATE_CODE_MAP.put("17", "Meghalaya");
		GST_STATE_CODE_MAP.put("15", "Mizoram");
		GST_STATE_CODE_MAP.put("13", "Nagaland");
		GST_STATE_CODE_MAP.put("21", "Orissa");
		GST_STATE_CODE_MAP.put("34", "Pondicherry");
		GST_STATE_CODE_MAP.put("03", "Punjab");
		GST_STATE_CODE_MAP.put("08", "Rajasthan");
		GST_STATE_CODE_MAP.put("11", "Sikkim");
		GST_STATE_CODE_MAP.put("33", "Tamil Nadu");
		GST_STATE_CODE_MAP.put("36", "Telangana");
		GST_STATE_CODE_MAP.put("16", "Tripura");
		GST_STATE_CODE_MAP.put("09", "Uttar Pradesh");
		GST_STATE_CODE_MAP.put("05", "Uttrakhand");
		GST_STATE_CODE_MAP.put("19", "West Bengal");
	}

	public static void validateGSTNumber(String gstNumber) {
		if (ObjectUtils.isEmpty(gstNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid gstNumber: " + gstNumber);
		}
		boolean foundValidStateCode = false;
		for (Entry<String, String> entry : GST_STATE_CODE_MAP.entrySet()) {
			if (StringUtils.startsWith(gstNumber, entry.getKey())) {
				foundValidStateCode = true;
				break;
			}
		}
		if (!foundValidStateCode) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid gstNumber: " + gstNumber);
		}
	}

	public static boolean isSameStateGSTNumber(String gstNumber1, String gstNumber2) {
		return StringUtils.equals(StringUtils.substring(gstNumber2, 0, 2), StringUtils.substring(gstNumber1, 0, 2));
	}

	public static Optional<String> getGSTNumberForBilledFrom(List<String> gstNumbers, String gstNumber2) {
		Optional<String> optional = Optional.empty();
		for (String gstNumber : gstNumbers) {
			if (isSameStateGSTNumber(gstNumber, gstNumber2))
				optional = Optional.of(gstNumber);
		}
		return optional;
	}

	public static long calculateInclusiveGst(Long amount) {
		long gstAmount = (long) ((amount * 18) / (100 + 18));
		return gstAmount;
	}

}
