package com.cz.platform.logging;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cz.platform.PlatformConstants;
import com.cz.platform.custom.events.CustomSpringEvent;
import com.cz.platform.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile(PlatformConstants.PROD_PROFILE)
public class TestSentryExceptionLogging {

	@EventListener(condition = "#event.name == 'TEST_SENTRY_EXCEPTION_LOGGING'")
	private void testSentryExceptoin(CustomSpringEvent event) throws ApplicationException {
		try {
			throw new Exception("This is a test exception.");
		} catch (Exception e) {
			log.error("test exceptoin", e);
		}
	}
}
