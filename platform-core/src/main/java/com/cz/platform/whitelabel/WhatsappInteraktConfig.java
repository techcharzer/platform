package com.cz.platform.whitelabel;

import java.io.Serializable;

import lombok.Data;

@Data
public class WhatsappInteraktConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7346130200407159488L;
	private String authkey;
	private NotificationList notificationList;
}
