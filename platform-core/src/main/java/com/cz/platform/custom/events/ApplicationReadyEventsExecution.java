package com.cz.platform.custom.events;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class ApplicationReadyEventsExecution {

	private CustomEventConfig customEventConfig;

	private ApplicationEventPublisher applicationEventPublisher;

	@EventListener(ApplicationReadyEvent.class)
	public void fillInMemoryHeaps() {
		Map<Integer, String> eventNames = new TreeMap<>();
		eventNames.putAll(customEventConfig.getListOfCustomeEvents());
		log.info("list of event names after sorting : {}", eventNames);
		for (String name : eventNames.values()) {
			CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, name);
			applicationEventPublisher.publishEvent(customSpringEvent);
		}
	}
}
