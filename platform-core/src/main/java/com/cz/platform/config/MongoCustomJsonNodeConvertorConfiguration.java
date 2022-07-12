package com.cz.platform.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MongoCustomJsonNodeConvertorConfiguration {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(new JsonNodeToDocumentConverter());
		converters.add(new DocumentToJsonNodeConverter());
		return new MongoCustomConversions(converters);
	}

	@WritingConverter
	public static class JsonNodeToDocumentConverter implements Converter<JsonNode, String> {

		@Autowired
		private ObjectMapper mapper = new ObjectMapper();

		public String convert(JsonNode source) {
			if (source == null) {
				return null;
			}

			try {
				return mapper.writeValueAsString(source);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	@ReadingConverter
	public static class DocumentToJsonNodeConverter implements Converter<String, JsonNode> {

		@Autowired
		private ObjectMapper mapper = new ObjectMapper();

		public JsonNode convert(String source) {
			if (source == null)
				return null;

			try {
				return mapper.readTree(source);
			} catch (IOException e) {
				throw new RuntimeException("Unable to parse String to JsonNode", e);
			}
		}
	}

}
