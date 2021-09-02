package com.cz.platform.config;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.cz.platform.exception.ApplicationException;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SentryConfig {

	@Value("${test.sentry-on-container-start-up:false}")
	private boolean testSentryExceptoin;

	@Autowired
	private Environment environment;

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	private SentryConfigProps props;

	@PostConstruct
	public void configureSentry() {
		if (!ObjectUtils.isEmpty(props.getSentryDsn())) {
			log.info("CONFIGURING sentry as sentry-dsn is present.");
			Sentry.init(options -> {
				String[] envs = environment.getActiveProfiles();
				String[] defaultProfiles = environment.getDefaultProfiles();
				String activeProfile = null;
				for (String env : envs) {
					log.info("env {}", env);
					activeProfile = env;
				}
				if (ObjectUtils.isEmpty(activeProfile)) {
					for (String env : defaultProfiles) {
						log.info("env {}", env);
						activeProfile = env;
					}
				}
				options.setServerName(appName);
				options.setEnvironment(activeProfile);
				options.setDsn(props.getSentryDsn());
				options.setBeforeSend((event, hint) -> {
					String className = event.getThrowable().getClass().getSimpleName();
					if (props.getExceptionIgnoreSet().contains(className)) {
						return null;
					} else {
						return event;
					}
				});
			});
			testSentryExceptoin();
		} else {
			log.info("SENTRY NOT CONFIGURED as sentry-dsn is missing.");
		}
	}

	private void testSentryExceptoin() throws ApplicationException {
		if (props.getTestSentryOnStartup()) {
			try {
				throw new Exception("Container restarted. And sentry logging is working fine.");
			} catch (Exception e) {
				log.error("Container restarted. And sentry logging is working fine.", e);
			}
		}
	}
}
