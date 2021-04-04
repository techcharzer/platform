package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

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
		List<Role> roles = new ArrayList<>();
		for (String role : props.getTestingRoles()) {
			roles.add(new Role(role));
		}
		String testingToken = provider.createToken("testingSSOToken", roles);
		log.info("testing Token : {}", testingToken);
	}
}