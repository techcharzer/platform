package org.charzer.platform.cors;

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
					log.info("CORS configuration : DISABLED");
					registry.addMapping("/**").allowedHeaders("*").allowedOrigins("*").allowedMethods("POST", "PUT",
							"GET");
				} else {
					log.info("CORS configuration : ENABLED");
				}
			}

		};
	}
}
