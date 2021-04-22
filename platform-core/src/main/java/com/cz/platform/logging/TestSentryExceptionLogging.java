package com.cz.platform.logging;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile(PlatformConstants.PROD_PROFILE)
public class TestSentryExceptionLogging {

	@EventListener(ApplicationReadyEvent.class)
	private void testSentryExceptoin() throws ApplicationException {
		try {
			throw new Exception("Container restarted. And sentry logging is working fine.");
		} catch (Exception e) {
			log.error("Container restarted. And sentry logging is working fine.", e);
		}
	}
}
