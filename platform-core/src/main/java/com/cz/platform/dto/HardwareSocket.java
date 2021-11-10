package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class HardwareSocket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 42362998463993412L;
	private String id;
	private String type;
	private Double power;
}
