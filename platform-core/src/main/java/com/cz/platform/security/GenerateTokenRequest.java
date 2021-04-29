package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
public class GenerateTokenRequest {
	private String client;
	private List<String> roles;
}
