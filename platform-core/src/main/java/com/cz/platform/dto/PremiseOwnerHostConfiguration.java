package com.cz.platform.dto;

import com.cz.platform.enums.HostTypeEnum;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.Data;

@Data
public class PremiseOwnerHostConfiguration implements HostConfiguration {
	private HostTypeEnum type = HostTypeEnum.PREMISE_OWNER;

	@Override
	public Double getProfitSharePercentageOnBooking() {
		throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
				"User is not a dealer. Please ask charzer team to make this user as dealer.");
	}

}
