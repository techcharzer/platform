package com.cz.platform.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RestTemplateConfig {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplateBuilder builder;

	@Bean
	@Primary
	public RestTemplate getInternalRestTemplate() {
		return getRestTemplate(3);
	}

	public RestTemplate getRestTemplate(int seconds) {
		return builder.setConnectTimeout(Duration.ofSeconds(seconds)) // Connection timeout
				.setReadTimeout(Duration.ofSeconds(seconds)) // Read timeout
				.additionalMessageConverters(new MappingJackson2HttpMessageConverter(mapper)).build();
	}

	@Bean(PlatformConstants.EXTERNAL_CLIENT)
	public RestTemplate getExternalRestTemplate() {
		return getRestTemplate(5);
	}

	@Bean(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	public RestTemplate getExternalSlowRestTemplate() {
		return getRestTemplate(60);
	}

}
