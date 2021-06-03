package com.cz.platform.security;

import java.io.Serializable;

import lombok.Data;

@Data
public class NetworkMapping implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8462771311926210572L;
	private String networkId;
	private Boolean isActive;
}
