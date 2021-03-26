package com.cz.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;

@Configuration
public class RestTemplateConfig {

	@Bean
	@Primary
	public RestTemplate getInternalRestTemplate() {
		return getRestTemplate(3000);
	}

	public RestTemplate getRestTemplate(int timeout) {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(timeout);
		httpRequestFactory.setConnectTimeout(timeout);
		httpRequestFactory.setReadTimeout(timeout);
		return new RestTemplate(httpRequestFactory);
	}

	@Bean(PlatformConstants.EXTERNAL_CLIENT)
	public RestTemplate getExternalRestTemplate() {
		return getRestTemplate(5000);
	}

	@Bean(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	public RestTemplate getExternalSlowRestTemplate() {
		return getRestTemplate(7000);
	}
}
