package com.cz.platform.utility;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cz.platform.clients.CustomRabbitMQTemplate;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;

import lombok.Data;

@Service
public class GenericAuditingService {

	@Autowired
	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;

	@Autowired
	private CustomRabbitMQTemplate template;

	@Value("${spring.application.name}")
	private String applicationName;

	public void updateValue(Class clazz, String id, UpdateRecord record, String createdBy, Instant time) {
		List<UpdateRecord> list = new ArrayList<>();
		list.add(record);
		updateValue(clazz, id, list, createdBy, time);
	}

	public void updateValue(Class clazz, String id, List<UpdateRecord> records, String createdBy, Instant time) {
		UpdateDiffRequest request = new UpdateDiffRequest();
		request.setCreatedAt(time);
		request.setCreatedBy(createdBy);
		request.setEntity(clazz.getSimpleName());
		request.setId(id);
		request.setRecords(records);
		template.convertAndSend(rabbitQueueConfiguration.getEntityAuditing(), request);
	}

	@Data
	public static class UpdateDiffRequest {
		private String id;
		private String entity;
		private String entityId;
		private List<UpdateRecord> records;
		private String createdBy;
		private Instant createdAt;
	}

	@Data
	public class UpdateRecord {
		private String key;
		private String oldValue;
		private String newValue;
	}

}
