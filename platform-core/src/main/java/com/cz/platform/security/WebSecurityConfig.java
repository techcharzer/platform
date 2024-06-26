package com.cz.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(0)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthService authService;

	@Autowired
	private ObjectMapper mapper;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("SECURITY CONFIGURED");
		// Disable CSRF (cross site request forgery)
		http.csrf().disable();
		http.formLogin().disable().logout().disable().httpBasic().disable();
		http.headers().frameOptions().disable();

		// No session will be created or used by spring security[
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.antMatcher("/secure/**").authorizeRequests().anyRequest().authenticated();

		// If a user try to access a resource without having enough permissions
		http.exceptionHandling().accessDeniedPage("/login");

		// Apply JWT
		AuthTokenFilter customFilter = new AuthTokenFilter(authService, mapper);
		http.addFilterBefore(customFilter, SecurityContextHolderAwareRequestFilter.class);

		http.cors();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}