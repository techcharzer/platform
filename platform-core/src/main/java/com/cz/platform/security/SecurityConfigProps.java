package com.cz.platform.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.SECURITY_CONFIG_PREFIX)
public class SecurityConfigProps {

	private String jwtSecretKey;
	private String newJwtSecretKey;
	private List<String> testingRoles = new ArrayList<>();
	private Map<String, String> creds = new HashMap<>();

}
