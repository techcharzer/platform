package com.cz.platform.charger.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OCPPVersion {
	JSON_16("1.6J");

	private String versionName;
}
