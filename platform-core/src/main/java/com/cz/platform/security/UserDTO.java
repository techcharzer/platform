package com.cz.platform.security;

import java.util.List;

public interface UserDTO {
	public String getUserId();

	public String getMobileNumber();

	public AllowedChargerNetwork getSociety();

	public List<RoleDTO> getRoles();
}
