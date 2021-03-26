package com.cz.platform.cors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfigugration {

	@Autowired
	private CorsConfigProps props;

	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				if (props.isDisabled()) {
					// TODO cors config needs to be updated so that api can be called from local as
					// well.
					log.info("CORS configuration : DISABLED");
					registry.addMapping("/**").allowedHeaders("*").allowedOriginPatterns(props.getAllowedOrigin())
							.allowedMethods("POST", "PUT", "GET");
				} else {
					log.info("CORS configuration : ENABLED");
				}
			}

		};
	}
}
