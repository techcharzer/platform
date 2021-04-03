package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {
	private String userName;
	private String password;
	private List<Role> roles;
}
