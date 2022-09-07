package com.cz.platform.whitelabel;

import java.io.Serializable;

import lombok.Data;

@Data
/**
 * this will have only white label specific notifications.
 * 
 * @author arun.chouhan
 *
 */
public class NotificationConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2754594944009906391L;
	private WhatsappInteraktConfig whatsappInteraktConfig;
	private SMSMessage91Config smsMessage91Config;
}
