package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Socket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 801258998463993412L;
	private String id;
	private String connectorId;
	private String socketType;
	private Double power;
}
