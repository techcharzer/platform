package com.cz.platform.security;

import java.time.ZoneId;
import java.util.List;

import com.cz.platform.enums.OperatingSystem;
import com.cz.platform.enums.UserType;

public interface UserDTO {
	public String getUserId();

	public String getMobileNumber();

	public List<RoleDTO> getRoles();

	public ZoneId getZoneId();

	public UserType getUserType();

	public boolean hasCZOAccess();

	public void validateCZOAccess();

	public String getSource();

	public OperatingSystem getOperatingSystem();
}
