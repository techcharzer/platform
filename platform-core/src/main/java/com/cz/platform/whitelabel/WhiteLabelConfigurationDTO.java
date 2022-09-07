package com.cz.platform.whitelabel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class WhiteLabelConfigurationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5944548174562180669L;
	private String appName;
	private String packageName;
	private String downloadLink;
	private Long walletInitializationCredits;
	private WhiteLabelAppTypeEnum type;
	private ReferralConfig referralConfig;
	private String ownerId;
	private NotificationConfig notificationConfig;
	private BannerConfig banner;
	private Set<String> dhAppViewersId = new HashSet<>();
	private List<LaunchedCity> launchedCities;
}
