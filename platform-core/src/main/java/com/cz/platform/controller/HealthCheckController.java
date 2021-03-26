package com.cz.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HealthCheckController {

	@GetMapping("/health")
	private String healthCheck() {
		log.trace("healthCheck");
		return "SUCCESS";
	}
}
