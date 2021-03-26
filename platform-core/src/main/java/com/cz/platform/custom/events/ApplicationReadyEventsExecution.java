package com.cz.platform.custom.events;

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
		log.info("list of event names after sorting : {}", customEventConfig.getListOfCustomeEvents());
		for (String name : customEventConfig.getListOfCustomeEvents()) {
			CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, name);
			applicationEventPublisher.publishEvent(customSpringEvent);
		}
	}
}
