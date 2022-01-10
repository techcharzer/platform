package com.cz.platform.notifications;

import lombok.Data;

@Data
public class PushNotificationTo implements NotificationTO {
	private String fcmToken;
}
