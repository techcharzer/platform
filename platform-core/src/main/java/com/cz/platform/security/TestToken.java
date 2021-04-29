package com.cz.platform.security;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.cz.platform.PlatformConstants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile(PlatformConstants.DEV_PROFILE)
@AllArgsConstructor
public class TestToken {

	private JwtTokenProvider provider;

	private SecurityConfigProps props;

	@PostConstruct
	private void fillMap() {
		String testingToken = provider.createToken("testingSSOToken", props.getTestingRoles());
		log.info("testing Token : {}", testingToken);
	}
}