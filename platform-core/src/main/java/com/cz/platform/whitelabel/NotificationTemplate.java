package com.cz.platform.whitelabel;

import lombok.Data;

@Data
/**
 * this will have only white label specific notifications.
 * 
 * @author arun.chouhan
 *
 */
public class NotificationTemplate {
	private String otp;
	private String bookingConfirmation;
}
