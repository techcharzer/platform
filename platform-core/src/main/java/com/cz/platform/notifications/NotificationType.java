package com.cz.platform.notifications;

import lombok.Getter;

@Getter
public enum NotificationType {
	GENERIC_NOTIFICATION, SOME_EVENT, FALLBACK_MESSAGE;

	public static NotificationType getEnum(String str) {
		for (NotificationType event : NotificationType.values()) {
			if (event.name().equalsIgnoreCase(str)) {
				return event;
			}
		}
		return null;
	}
}
