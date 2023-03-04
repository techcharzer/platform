package com.cz.platform.security;

import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.cz.platform.PlatformConstants;
import com.cz.platform.enums.OperatingSystem;
import com.cz.platform.enums.UserType;
import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;

import lombok.Data;

@Data
class LoggedInUser implements UserDTO {

	private String userId;
	private String mobileNumber;
	private List<RoleDTO> roles;
	private UserType userType;
	private String chargePointOperatorId;
	private OperatingSystem operatingSystem;

	@Override
	public ZoneId getZoneId() {
		return ZoneId.of(PlatformConstants.CURRENT_TIME_ZONE);
	}

	@Override
	public boolean hasCZOAccess() {
		return UserType.CZO.equals(this.userType);
	}

	@Override
	public void validateCZOAccess() {
		if (BooleanUtils.isFalse(hasCZOAccess())) {
			throw new AuthenticationException(PlatformExceptionCodes.ACCESS_DENIED.getCode(),
					"Does not have CZO access");
		}
	}

}
