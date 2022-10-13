package com.cz.platform.telephone.calls;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app.config.callerdesk")
public class CallerDeskConfig {
	private String authkey;
	private List<String> deskPhoneNumbers;
}
