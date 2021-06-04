package com.cz.platform.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
class LoggedInUser implements Serializable, UserDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148729374372L;
	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;
	private ProtectedChargerNetworkUserMapping society;

}
