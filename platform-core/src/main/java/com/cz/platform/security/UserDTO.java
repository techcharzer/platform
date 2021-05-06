package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {
	private String mobileNumber;
	private String userId;
	private List<RoleDTO> roles;
}
