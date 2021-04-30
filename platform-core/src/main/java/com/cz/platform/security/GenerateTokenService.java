package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cz.platform.security.AuthService;
import com.cz.platform.security.Role;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GenerateTokenService {

	private AuthService provider;

	public GenerateTokenResponse generateToken(GenerateTokenRequest request) {
		log.info("generateToken request : {}", request);
		GenerateTokenResponse response = new GenerateTokenResponse();
		response.setToken(provider.createToken(request.getClient(), request.getRoles()));
		return response;
	}
}
