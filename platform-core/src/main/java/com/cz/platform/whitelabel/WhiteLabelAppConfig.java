package com.cz.platform.whitelabel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.cz.platform.PlatformConstants;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.SECURITY_CONFIG_PREFIX)
public class WhiteLabelAppConfig {

	private Map<WhiteLabelAppTypeEnum, WhiteLabelConfigurationDTO> mapOfAppSourceAndWhiteLabelAppConfig = new HashMap<>();

}
