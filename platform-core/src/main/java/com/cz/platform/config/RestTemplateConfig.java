package com.cz.platform.config;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RestTemplateConfig {

	@Autowired
	private ObjectMapper mapper;

	@Bean
	@Primary
	public RestTemplate getInternalRestTemplate() {
		return getRestTemplate(3000);
	}

	public RestTemplate getRestTemplate(int timeout) {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new CustomHttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(timeout);
		httpRequestFactory.setConnectTimeout(timeout);
		httpRequestFactory.setReadTimeout(timeout);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(mapper);

		RestTemplate template = new RestTemplate(httpRequestFactory);
		template.getMessageConverters().add(0, converter);
		return template;
	}

	@Bean(PlatformConstants.EXTERNAL_CLIENT)
	public RestTemplate getExternalRestTemplate() {
		return getRestTemplate(5000);
	}

	@Bean(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	public RestTemplate getExternalSlowRestTemplate() {
		return getRestTemplate(60000);
	}

	private static final class CustomHttpComponentsClientHttpRequestFactory
			extends HttpComponentsClientHttpRequestFactory {
		@Override
		protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
			if (HttpMethod.GET.equals(httpMethod)) {
				return new HttpEntityEnclosingGetRequestBase(uri);
			}
			return super.createHttpUriRequest(httpMethod, uri);
		}
	}

	private static final class HttpEntityEnclosingGetRequestBase extends HttpEntityEnclosingRequestBase {
		public HttpEntityEnclosingGetRequestBase(final URI uri) {
			super.setURI(uri);
		}

		@Override
		public String getMethod() {
			return HttpMethod.GET.name();
		}
	}
}
