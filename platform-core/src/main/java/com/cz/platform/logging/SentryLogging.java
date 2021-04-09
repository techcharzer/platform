package com.cz.platform.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.cz.platform.PlatformConstants;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile(PlatformConstants.PROD_PROFILE)
class SentryLogging {

	@Value("${sentry.dsn}")
	private String dsn;

	@Value("${spring.application.name}")
	private String applicationName;

	@Bean
	public void sentryProps() {
		log.info("SENTRY CONFIGURED");
		Sentry.init(options -> {
			options.setDsn(dsn);
			options.setServerName(applicationName);
		});
	}
}
