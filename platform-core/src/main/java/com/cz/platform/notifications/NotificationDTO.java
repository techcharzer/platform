package com.cz.platform.notifications;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class NotificationDTO {

	private String id;
	private String mobile;
	private NotificationType eventType;
	private Channel channel;
	private Map<String, String> eventData;
	private boolean isUnidirectional;
	private List<String> templates;

}
