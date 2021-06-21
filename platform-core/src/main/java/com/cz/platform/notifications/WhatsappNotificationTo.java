package com.cz.platform.notifications;

import java.io.Serializable;

import lombok.Data;

@Data
public class WhatsappNotificationTo implements NotificationTO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7008315795908771996L;
	private String phone;
	private Boolean isWaitingForResponse = Boolean.FALSE;
}
