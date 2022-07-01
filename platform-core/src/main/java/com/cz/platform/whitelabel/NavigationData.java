package com.cz.platform.whitelabel;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class NavigationData {
	private String to;
	private Map<String, String> metaData = new HashMap<>();
}
