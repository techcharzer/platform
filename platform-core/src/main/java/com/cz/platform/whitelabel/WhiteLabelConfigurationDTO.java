package com.cz.platform.whitelabel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class WhiteLabelConfigurationDTO {
	private String appName;
	private String packageName;
	private WhiteLabelAppTypeEnum type;
	private ReferralConfig referralConfig;
	private NotificationTemplate notificationTemplates;
	private String ownerId;
	private BannerConfig banner;
	private Set<String> dhAppViewersId = new HashSet<>();
	private List<LaunchedCity> launchedCities;
}
