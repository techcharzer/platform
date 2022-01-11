package com.cz.platform.cron;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public abstract class GenericCronService {

	protected static final Map<String, CustomCronConsumer> MAP_OF_KEY_CRONS = new HashMap<>();

	@FunctionalInterface
	public interface CustomCronConsumer {
		void executeKeyCron();
	}

	public void executeCron(String key) {
		log.info("cron execution started: {}", key);
		if (!MAP_OF_KEY_CRONS.containsKey(key)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid cron key for execution");
		}
		try {
			MAP_OF_KEY_CRONS.get(key).executeKeyCron();
		} catch (Exception e) {
			log.error("error occured while executing the cron: {}", key, e);
		}
		log.info("cron execution completed: {}", key);
	}

}
