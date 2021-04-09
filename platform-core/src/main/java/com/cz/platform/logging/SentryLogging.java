package com.cz.platform.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile(value = { "qa", "prod" })
class SentryLogging {

	@Value("${sentry.dsn}")
	private String dsn;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.profiles.active}")
	private String env;

	@Bean
	public void sentryProps() {
		log.info("SENTRY CONFIGURED");
		Sentry.init(options -> {
			options.setDsn(dsn);
			options.setEnvironment(env);
			options.setServerName(applicationName);
		});
	}
}
