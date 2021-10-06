package com.cz.platform.databind;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
		String str = p.getText();
		try {
			return LocalDateTime.parse(str, PlatformConstants.DATE_TIME_FORMATTER);
		} catch (DateTimeParseException e) {
			log.info("error: ", e);
			throw new ApplicationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid format");
		}
	}
}