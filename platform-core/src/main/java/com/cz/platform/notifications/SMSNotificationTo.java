package com.cz.platform.notifications;

import java.io.Serializable;

import lombok.Data;

@Data
public class SMSNotificationTo implements NotificationTO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5908222877037206420L;
	private String mobileNumber;
}
