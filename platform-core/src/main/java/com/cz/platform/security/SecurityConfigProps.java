package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.SECURITY_CONFIG_PREFIX)
public class SecurityConfigProps {

	private String jwtSecretKey;
	private List<String> testingRoles = new ArrayList<>();

}
