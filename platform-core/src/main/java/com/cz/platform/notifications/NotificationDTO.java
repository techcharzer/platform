package com.cz.platform.notifications;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class NotificationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 394202139477492146L;
	private String id;
	private Channel channel;
	private NotificationType type;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "channel")
	@JsonSubTypes({ @Type(value = WhatsappNotificationTo.class, name = "WHATSAPP"),
			@Type(value = SMSNotificationTo.class, name = "SMS") })
	private NotificationTO to;
	private Map<String, String> data;
	private List<String> templates;

}
