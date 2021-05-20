package com.cz.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChargerUsageType {
	/*
	 * It means the charger will be public and visible on website and in map view of
	 * mobile app. it is used when the charger is hosted in restaurant, kirana
	 * shops, gym and mall.
	 */
	PUBLIC,
	/*
	 * It means the charger will be protected and NOT visible on website and in map
	 * view of mobile app. But it cannot be booked by public. unless you are
	 * authorized by the society or the owner.
	 */
	PROTECTED,
	/*
	 * It means the charger will be a private charger and can be used only by family
	 * members.
	 */
	PRIVATE;

}
