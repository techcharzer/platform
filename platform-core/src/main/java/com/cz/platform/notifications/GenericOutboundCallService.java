package com.cz.platform.notifications;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cz.platform.clients.CustomRabbitMQTemplate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@AllArgsConstructor
public class GenericOutboundCallService {

	private CustomRabbitMQTemplate rabbitTemplate;

	private ApplicationContext applicationContext;

	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;

	public void initiateOutboundCall(String customerAgentNumber, String customerNumber, ISourceType source,
			String sourceId) {
		OutBoundCallRequest outBoundRequest = new OutBoundCallRequest();
		outBoundRequest.setAgentNumber(customerAgentNumber);
		outBoundRequest.setCustomerNumber(customerNumber);
		SourceData data = new SourceData();
		data.setAppName(applicationContext.getId());
		data.setType(source);
		data.setId(sourceId);
		outBoundRequest.setSource(data);
		rabbitTemplate.convertAndSend(rabbitQueueConfiguration.getInitiateOutboundCall(), outBoundRequest);
	}

	@Data
	public static class OutBoundCallRequest {
		private String agentNumber;
		private String customerNumber;
		private SourceData source;
	}

	@Data
	public static class SourceData {
		private ISourceType type;
		private String id;
		private String appName;
	}

	public static interface ISourceType {
		String getTypeName();
	}

}
