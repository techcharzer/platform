package com.cz.platform.security;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProtectedChargerNetworkUserMapping implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1702308109730866850L;
	private NetworkMapping user;
	private NetworkMapping admin;
}