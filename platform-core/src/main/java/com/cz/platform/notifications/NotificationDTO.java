package com.cz.platform.notifications;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class NotificationDTO {

	private String id;
	private Channel channel;
	private String chargePointOperatorId;
	private NotificationType type;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "channel")
	@JsonSubTypes({ @Type(value = WhatsappNotificationTo.class, name = "WHATSAPP"),
			@Type(value = SMSNotificationTo.class, name = "SMS"),
			@Type(value = PushNotificationTo.class, name = "DATA_PUSH"),
			@Type(value = PushNotificationTo.class, name = "PUSH_NOTIFICATION") })
	private NotificationTO to;
	private Map<String, String> data;
	private List<String> templates;

}
