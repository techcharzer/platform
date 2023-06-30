package com.cz.platform.security;

import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.enums.OperatingSystem;
import com.cz.platform.enums.UserType;

import lombok.Data;

@Data
class LoggedInUser implements UserDTO {

	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;
	// this fields is equivalent to login from.
	private UserType userType;
	private String chargePointOperatorId;
	private OperatingSystem operatingSystem;

	@Override
	public ZoneId getZoneId() {
		return ZoneId.of(PlatformConstants.CURRENT_TIME_ZONE);
	}

	@Override
	public boolean isLogInFrom(UserType logInFrom) {
		return ObjectUtils.isEmpty(logInFrom) ? false : logInFrom.equals(this.userType);
	}

}
