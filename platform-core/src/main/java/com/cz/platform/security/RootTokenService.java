package com.cz.platform.security;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.cz.platform.custom.events.CustomSpringEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RootTokenService {

	private AuthService provider;

	private SecurityConfigProps props;

	@EventListener(condition = "#event.name == 'GENERATE_ROOT_TOKEN'")
	private void fillMap(CustomSpringEvent event) {
		String testingToken = provider.createToken("root", props.getTestingRoles());
		log.info("root Token : {}", testingToken);
	}
}