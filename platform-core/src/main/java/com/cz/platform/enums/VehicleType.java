package com.cz.platform.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VehicleType {
	TWO_WHEELER("2 Wheeler"), THREE_WHEELER("3 Wheeler"), FOUR_WHEELER("4 Wheeler");

	private String displayText;

	private final static Map<String, VehicleType> ENUM_MAP = new HashMap<>();

	static {
		for (VehicleType type : VehicleType.values()) {
			ENUM_MAP.put(type.name(), type);
		}
	}

	public static VehicleType getEnum(String str) {
		return ENUM_MAP.get(str);
	}
}
