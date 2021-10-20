package com.cz.platform.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SocietyBlock implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -327701659721339830L;
	private String name;
	private String code;
	private List<Apartment> apartments;
}
