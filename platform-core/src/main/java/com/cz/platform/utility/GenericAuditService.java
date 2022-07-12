package com.cz.platform.utility;

import java.time.Instant;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;
import com.cz.platform.security.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@AllArgsConstructor
public class GenericAuditService {

	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;
	private CustomRabbitMQTemplate template;
	private ObjectMapper mapper;

	public <T> void auditEvent(String id, String reason, T data, Class<T> clazz) {
		auditEvent(id, getLoggedInUserId(), Instant.now(), reason, data, clazz);
	}

	public <T> void auditEvent(String id, String author, Instant time, String reason, T data, Class<T> clazz) {
		JsonNode snapshotData = mapper.convertValue(data, JsonNode.class);
		AuditMetaData metaData = new AuditMetaData();
		metaData.setCreatedAt(time);
		metaData.setCreatedBy(author);
		metaData.setEntity(clazz.getSimpleName());
		metaData.setEntityId(id);
		metaData.setReason(reason);

		AuditRequest request = new AuditRequest();
		request.setCurrSnapshot(snapshotData);
		request.setMetaData(metaData);
		template.convertAndSend(rabbitQueueConfiguration.getEntityAuditing(), request);
	}

	@Data
	public static class AuditMetaData {
		private String entity;
		private String entityId;
		private String reason;
		private String createdBy;
		private Instant createdAt;
	}

	@Data
	public static class AuditRequest {
		private AuditMetaData metaData;
		private JsonNode currSnapshot;
	}

	public static String getLoggedInUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (ObjectUtils.isEmpty(context)) {
			return null;
		}
		if (ObjectUtils.isEmpty(context.getAuthentication())) {
			return null;
		}
		if (ObjectUtils.isEmpty(context.getAuthentication().getPrincipal())) {
			return null;
		}
		return ((UserDTO) context.getAuthentication().getPrincipal()).getUserId();
	}

}
