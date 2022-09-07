package com.cz.platform.whitelabel;

import java.io.Serializable;

import lombok.Data;

@Data
public class SMSMessage91Config implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6771264122564016219L;
	private String authkey;
	private String senderId;
	private String route;
	private NotificationList notificationList;
}
