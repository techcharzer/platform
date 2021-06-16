package com.cz.platform.utility;

import org.springframework.util.ObjectUtils;

public final class CommonUtility {

	private CommonUtility() {
	}

	public static String getUrlSlug(String str) {
		return ObjectUtils.isEmpty(str) ? str : replaceSpaceWithHyphen(str.toLowerCase());
	}

	public static String replaceSpaceWithHyphen(String str) {
		return ObjectUtils.isEmpty(str) ? str : str.replace(" ", "-");
	}
}
