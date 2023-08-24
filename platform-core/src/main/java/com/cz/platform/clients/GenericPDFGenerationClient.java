package com.cz.platform.clients;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.notifications.GenericRabbitQueueConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class GenericPDFGenerationClient {

	private CustomRabbitMQTemplate rabbitMqTemplate;

	private GenericRabbitQueueConfiguration rabbitQueConfiguration;

	public void generatePdfForTheInvoice(GeneratePDFRequest request) {
		log.info("start booking request:{}", request);
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		rabbitMqTemplate.convertAndSend(rabbitQueConfiguration.getPdfGenerationAndUpload(), request);
	}

	@Data
	public static class GeneratePDFRequest {
		private String generationStrategy;
		private String uniqueId;
		private String chargePointOperatorId;
		private IPDFData data;
	}

	public static interface IPDFData {

	}
}