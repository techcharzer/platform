package com.cz.platform.security;

import java.util.List;

import lombok.Data;

@Data
public class RoleDTO {
	private String roleId;
	private List<String> permissions;
}
