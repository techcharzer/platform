package com.cz.platform.whitelabel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class NavigationData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 390437537267565199L;
	private String to;
	private OpenIn openIn;
	private Map<String, String> metaData = new HashMap<>();

	public static enum OpenIn {
		MOBILE_APP, INTERNET_BROWSER
	}
}
