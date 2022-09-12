package com.cz.platform.whitelabel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app.config")
public class WhiteLabelAppConfig {

	private Map<WhiteLabelAppTypeEnum, WhiteLabelConfigurationDTO> whiteLabelConfiguration = new HashMap<>();

}
