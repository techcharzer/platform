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

	public void initiateOutboundCall(String customerAgentNumber, String customerNumber) {
		OutBoundCallRequest outBoundRequest = new OutBoundCallRequest();
		outBoundRequest.setAgentNumber(customerAgentNumber);
		outBoundRequest.setCustomerNumber(customerNumber);
		outBoundRequest.setSourceApp(applicationContext.getDisplayName());
		rabbitTemplate.convertAndSend(rabbitQueueConfiguration.getInitiateOutboundCall(), outBoundRequest);
	}

	@Data
	public static class OutBoundCallRequest {
		private String agentNumber;
		private String customerNumber;
		private String sourceApp;
	}

}
