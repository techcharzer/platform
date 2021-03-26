package com.cz.platform.custom.events;

import org.springframework.context.ApplicationEvent;

public class CustomSpringEvent extends ApplicationEvent {

	public String name;

	public CustomSpringEvent(Object source, String name) {
		super(source);
		this.name = name;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362783337645667L;

}
