package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
class LoggedInUser implements UserDTO {

	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;

}
