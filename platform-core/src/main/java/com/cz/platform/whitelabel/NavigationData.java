package com.cz.platform.whitelabel;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class NavigationData {
	private String to;
	private OpenIn openIn;
	private Map<String, String> metaData = new HashMap<>();

	public static enum OpenIn {
		MOBILE_APP, BROWSER
	}
}
