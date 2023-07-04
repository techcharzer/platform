package com.cz.platform.enums;

import org.apache.commons.lang3.ObjectUtils;

public enum UserType {
	CZO, CUSTOMER, INTERNAL_SERVICE, CMS;

	public static UserType getLogInFrom(LogInFrom logInFrom) {
		if (ObjectUtils.isEmpty(logInFrom)) {
			return null;
		}
		switch (logInFrom) {
		case DH_APP:
		case MOBILE_APP:
			return CUSTOMER;
		case CZO:
		case FLEET_GROUP:
		case CORPORATE_OFFICE_GROUP:
		case SOCIETY_GROUP:
			return CZO;
		case CMS:
			return CMS;
		case INTERNAL_SERVICE:
			return INTERNAL_SERVICE;
		default:
			break;
		}
		return null;
	}

}
