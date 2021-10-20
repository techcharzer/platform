package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Apartment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -681021507174414212L;
	private String name;
	private String code;
}
