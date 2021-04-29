package com.cz.platform.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class GenerateTokenController {

	private GenerateTokenService tokenService;
	
	@PostMapping("${app.config.security.generateTokenUrl}")
	private GenerateTokenResponse generateJwtToken(@RequestBody GenerateTokenRequest request) {
		return tokenService.generateToken(request);
	}
}
