package com.cz.platform.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class LoggedInUser implements Serializable, UserDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148729374372L;
	private String userId;
	private String mobileNumber;
	private ProtectedChargerNetworkUserMapping society;
	private List<RoleDTO> roles;

}
