package com.cz.platform.notifications;

import lombok.Data;

@Data
public class WhatsappNotificationTo implements NotificationTO {
	private String phone;
	private Boolean isWaitingForResponse = Boolean.FALSE;
}
