package com.cz.platform.whitelabel;

import java.io.Serializable;

import lombok.Data;

@Data
public class NotificationList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6114185853962643483L;
	private Boolean otp;
	private Boolean bookingConfirmationCustomer;
	private Boolean bookingConfirmationHost;
	private Boolean bookingCompletedCustomer;
	private Boolean bookingCompletedHost;
	private Boolean chargerLive;
}
