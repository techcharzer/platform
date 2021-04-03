package com.cz.platform.security;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role implements GrantedAuthority {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2211626756942145656L;
	private String role;
	public String getAuthority() {
		return role;
	}
}