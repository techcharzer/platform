package com.cz.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {

	@Autowired
	private AuthService authService;

	@Autowired
	private ObjectMapper mapper;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.info("Securing actuator endpoints");
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/actuator/**").hasRole("ACTUATOR_ENDPOINTS").anyRequest().authenticated())
				.addFilterBefore(new AuthTokenFilter(authService, mapper),
						SecurityContextHolderAwareRequestFilter.class);
		return http.build();
	}

}