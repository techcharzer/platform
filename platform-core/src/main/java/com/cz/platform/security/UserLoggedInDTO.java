package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
public class UserLoggedInDTO {
	private String mobileNumber;
	private String userId;
	private String societyId;
	private List<RoleDTO> roles;
}
