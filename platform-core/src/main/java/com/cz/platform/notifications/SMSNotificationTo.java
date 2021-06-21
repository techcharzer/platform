package com.cz.platform.notifications;

import lombok.Data;

@Data
public class SMSNotificationTo implements NotificationTO {
	private String mobileNumber;
}
