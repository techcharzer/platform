package com.cz.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Order(-1)
@Slf4j
@Configuration
public final class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthService authService;

	@Autowired
	private ObjectMapper mapper;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("securing actuator endpoints");
		http.requestMatchers().antMatchers("/actuator/**").and().authorizeRequests().anyRequest()
				.hasRole("ACTUATOR_ENDPOINTS");
		AuthTokenFilter customFilter = new AuthTokenFilter(authService, mapper);
		http.addFilterBefore(customFilter, SecurityContextHolderAwareRequestFilter.class);
	}

}