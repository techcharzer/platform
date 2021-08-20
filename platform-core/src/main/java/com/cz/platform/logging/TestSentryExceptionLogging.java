package com.cz.platform.logging;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cz.platform.custom.events.CustomSpringEvent;
import com.cz.platform.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TestSentryExceptionLogging {

	@Value("${test.sentry-on-container-start-up:false}")
	private boolean testSentryExceptoin;

	@PostConstruct
	private void testSentryExceptoin() throws ApplicationException {
		if (testSentryExceptoin) {
			try {
				throw new Exception("Container restarted. And sentry logging is working fine.");
			} catch (Exception e) {
				log.error("Container restarted. And sentry logging is working fine.", e);
			}
		}
	}
}
