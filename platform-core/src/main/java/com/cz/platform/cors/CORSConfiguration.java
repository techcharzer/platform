package com.cz.platform.cors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CORSConfiguration {

	@Autowired
	private CorsConfigProps props;

	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				if (!ObjectUtils.isEmpty(props.getAllowedOrigins())) {
					log.info("CORS configuration allowedOrigin : {}", props.getAllowedOrigins());
					CorsRegistration registration = registry.addMapping("/**");
					registration.allowedHeaders("*");
					int i = 0;
					if (props.getAllowedOrigins().size() > 6) {
						throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
								"Invalid cors configuration. max allowed 6 allowed origins.");
					}
					if (props.getAllowedOrigins().size() == 1) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++));
					} else if (props.getAllowedOrigins().size() == 2) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++));
					} else if (props.getAllowedOrigins().size() == 3) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++));
					} else if (props.getAllowedOrigins().size() == 4) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++));
					} else if (props.getAllowedOrigins().size() == 5) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++));
					} else if (props.getAllowedOrigins().size() == 6) {
						registration.allowedOriginPatterns(props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++), props.getAllowedOrigins().get(i++),
								props.getAllowedOrigins().get(i++));
					}
					registration.allowedMethods("POST", "PUT", "GET", "OPTIONS", "DELETE");
					registration.allowCredentials(true);
				} else {
					registry.addMapping("/**").allowedHeaders("*").allowedOrigins("*").allowedMethods("POST", "PUT",
							"GET", "DELETE");
				}
			}
		};
	}

}
