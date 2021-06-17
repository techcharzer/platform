package com.cz.platform.notifications;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class GlobalNotificationService {

	private RabbitTemplate rabbitTemplate;
	
	
	
//	public void sendGenericNotification(String mobile, Map<String, String> data, List<String> templates) {
//		NotificationDTO notificationDTO  = new NotificationDTO();
//		notificationDTO.setId(UUID.randomUUID().toString());
//		notificationDTO.setEventType(NotificationType.GENERIC_NOTIFICATION);
//		notificationDTO.setMobile(mobile);
//		notificationDTO.setTemplates(templates);
//		notificationDTO.setUnidirectional(true);
//		notificationDTO.setEventData(data);
//		sendNotification(notificationDTO);
//	}
//	
//	public void sendNotification(NotificationDTO notification) {
//		validateSendNotification(notification);
//		rabbitTemplate.convertAndSend();
//	}
//
//	private void validateSendNotification(NotificationDTO notification) {
//		if(notification.get)
//		
//	}
}
